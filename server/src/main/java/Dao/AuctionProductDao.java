package Dao;

import Database.DatabaseConnection;
import Dto.AuctionProductItem;
import Dto.BidHistoryPoint;
import Dto.AuctionProductRequest;
import Dto.AuctionProductResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuctionProductDao {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final long MIN_BID_INTERVAL_MILLIS = 100L;

    public boolean create(AuctionProductRequest request) {
        if (!ensureTablesExist()) {
            return false;
        }

        String sql = """
                INSERT INTO auction_products
                (seller_username, category, product_name, description, start_price, current_price, buy_now_price, end_time, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());

            statement.setString(1, request.getSellerUsername());
            statement.setString(2, request.getCategory());
            statement.setString(3, request.getProductName());
            statement.setString(4, request.getDescription());
            statement.setInt(5, request.getStartPrice());
            statement.setInt(6, request.getStartPrice());
            statement.setInt(7, request.getBuyNowPrice());
            statement.setTimestamp(8, Timestamp.valueOf(endTime));
            statement.setString(9, resolveStatus(endTime));

            return statement.executeUpdate() > 0;
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AuctionProductItem> findAll() {
        List<AuctionProductItem> items = new ArrayList<>();
        if (!ensureTablesExist()) {
            return items;
        }

        String sql = """
                SELECT id, category, product_name, description, current_price, buy_now_price, end_time, status, seller_username
                FROM auction_products
                ORDER BY created_at DESC, id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                items.add(new AuctionProductItem(
                        rs.getInt("id"),
                        rs.getString("category"),
                        rs.getString("product_name"),
                        rs.getString("description"),
                        rs.getInt("current_price"),
                        rs.getInt("buy_now_price"),
                        endTime.toString(),
                        resolveStatus(endTime),
                        rs.getString("seller_username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public AuctionProductResponse placeBid(int productId, int bidAmount) {
        if (!ensureTablesExist()) {
            return new AuctionProductResponse(false, "Không thể truy cập dữ liệu sản phẩm.");
        }

        String selectSql = """
                SELECT current_price, buy_now_price, end_time
                FROM auction_products
                WHERE id = ?
                FOR UPDATE
                """;
        String latestBidSql = """
                SELECT bid_time
                FROM auction_product_bids
                WHERE product_id = ?
                ORDER BY bid_time DESC, id DESC
                LIMIT 1
                """;
        String updateSql = """
                UPDATE auction_products
                SET current_price = ?, status = ?
                WHERE id = ?
                """;
        String insertBidSql = """
                INSERT INTO auction_product_bids (product_id, bid_amount, bid_time)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                 PreparedStatement latestBidStatement = connection.prepareStatement(latestBidSql);
                 PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                 PreparedStatement insertBidStatement = connection.prepareStatement(insertBidSql)) {
                selectStatement.setInt(1, productId);

                try (ResultSet rs = selectStatement.executeQuery()) {
                if (!rs.next()) {
                    connection.rollback();
                    return new AuctionProductResponse(false, "Không tìm thấy sản phẩm.");
                }

                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                String currentStatus = resolveStatus(endTime);
                if (!"OPEN".equals(currentStatus)) {
                    updateStatus(connection, productId, currentStatus);
                    connection.commit();
                    return new AuctionProductResponse(false, "Phiên đấu giá đã kết thúc.");
                }

                int currentPrice = rs.getInt("current_price");
                int buyNowPrice = rs.getInt("buy_now_price");
                int minimumBid = calculateMinimumBid(currentPrice);
                if (bidAmount < minimumBid) {
                    connection.rollback();
                    return new AuctionProductResponse(false,
                            "Giá đặt phải lớn hơn hoặc bằng " + minimumBid + ".");
                }

                latestBidStatement.setInt(1, productId);
                LocalDateTime latestBidTime = null;
                try (ResultSet latestBidResult = latestBidStatement.executeQuery()) {
                    if (latestBidResult.next() && latestBidResult.getTimestamp("bid_time") != null) {
                        latestBidTime = latestBidResult.getTimestamp("bid_time").toLocalDateTime();
                    }
                }

                int nextPrice = Math.min(bidAmount, buyNowPrice);
                String nextStatus = nextPrice >= buyNowPrice ? "FINISHED" : "OPEN";
                LocalDateTime bidTime = LocalDateTime.now();

                if (latestBidTime != null
                        && Duration.between(latestBidTime, bidTime).toMillis() < MIN_BID_INTERVAL_MILLIS) {
                    connection.rollback();
                    return new AuctionProductResponse(false, "Vui lòng chờ 100ms trước khi đặt giá tiếp theo.");
                }

                updateStatement.setInt(1, nextPrice);
                updateStatement.setString(2, nextStatus);
                updateStatement.setInt(3, productId);

                if (updateStatement.executeUpdate() <= 0) {
                    connection.rollback();
                    return new AuctionProductResponse(false, "Không thể cập nhật giá sản phẩm.");
                }

                insertBidStatement.setInt(1, productId);
                insertBidStatement.setInt(2, nextPrice);
                insertBidStatement.setTimestamp(3, Timestamp.valueOf(bidTime));
                insertBidStatement.executeUpdate();

                connection.commit();

                if ("FINISHED".equals(nextStatus)) {
                    return new AuctionProductResponse(
                            true,
                            "Đặt giá thành công. Sản phẩm đã đạt giá chốt.",
                            nextPrice,
                            nextStatus,
                            bidTime.format(DATE_TIME_FORMATTER)
                    );
                }
                return new AuctionProductResponse(
                        true,
                        "Đặt giá thành công.",
                        nextPrice,
                        nextStatus,
                        bidTime.format(DATE_TIME_FORMATTER)
                );
            }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuctionProductResponse(false, "Không thể cập nhật giá sản phẩm.");
        }
    }

    public List<BidHistoryPoint> findBidHistory(int productId) {
        List<BidHistoryPoint> history = new ArrayList<>();
        if (!ensureTablesExist()) {
            return history;
        }

        String productSql = """
                SELECT start_price, created_at
                FROM auction_products
                WHERE id = ?
                """;
        String bidSql = """
                SELECT bid_amount, bid_time
                FROM auction_product_bids
                WHERE product_id = ?
                ORDER BY bid_time ASC, id ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement productStatement = connection.prepareStatement(productSql);
             PreparedStatement bidStatement = connection.prepareStatement(bidSql)) {
            productStatement.setInt(1, productId);
            try (ResultSet productResult = productStatement.executeQuery()) {
                if (!productResult.next()) {
                    return history;
                }

                LocalDateTime createdAt = productResult.getTimestamp("created_at").toLocalDateTime();
                history.add(new BidHistoryPoint(
                        productResult.getInt("start_price"),
                        createdAt.format(DATE_TIME_FORMATTER)
                ));
            }

            bidStatement.setInt(1, productId);
            try (ResultSet bidResult = bidStatement.executeQuery()) {
                while (bidResult.next()) {
                    LocalDateTime bidTime = bidResult.getTimestamp("bid_time").toLocalDateTime();
                    history.add(new BidHistoryPoint(
                            bidResult.getInt("bid_amount"),
                            bidTime.format(DATE_TIME_FORMATTER)
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    private boolean ensureTablesExist() {
        return ensureProductTableExists() && ensureBidHistoryTableExists();
    }

    private boolean ensureProductTableExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS auction_products (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    seller_username VARCHAR(255) NOT NULL,
                    category VARCHAR(100) NOT NULL,
                    product_name VARCHAR(255) NOT NULL,
                    description TEXT,
                    start_price INT NOT NULL,
                    current_price INT NOT NULL,
                    buy_now_price INT NOT NULL,
                    end_time DATETIME NOT NULL,
                    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean ensureBidHistoryTableExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS auction_product_bids (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    product_id INT NOT NULL,
                    bid_amount INT NOT NULL,
                    bid_time DATETIME NOT NULL,
                    INDEX idx_auction_product_bids_product_time (product_id, bid_time, id)
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String resolveStatus(LocalDateTime endTime) {
        return endTime.isBefore(LocalDateTime.now()) ? "FINISHED" : "OPEN";
    }

    private int calculateMinimumBid(int currentPrice) {
        return (int) Math.ceil(currentPrice * 1.02d);
    }

    private void updateStatus(Connection connection, int productId, String status) throws SQLException {
        String sql = """
                UPDATE auction_products
                SET status = ?
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }
    }
}
