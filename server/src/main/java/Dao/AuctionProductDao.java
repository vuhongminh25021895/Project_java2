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
    private static final long ANTI_SNIPING_THRESHOLD_MINUTES = 15L;
    private static final long ANTI_SNIPING_EXTENSION_MINUTES = 5L;
    private static final int ADMIN_COMMISSION_PERCENT = 20;
    private final UserDao userDao = new UserDao();

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

        settleExpiredAuctions();

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
                        resolveStatus(endTime, rs.getString("status")),
                        rs.getString("seller_username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public AuctionProductResponse placeBid(int productId, int bidAmount, String bidderUsername) {
        if (!ensureTablesExist()) {
            return new AuctionProductResponse(false, "Không thể truy cập dữ liệu sản phẩm.");
        }

        if (bidderUsername == null || bidderUsername.isBlank() || "Guest".equalsIgnoreCase(bidderUsername)) {
            return new AuctionProductResponse(false, "Vui lòng đăng nhập trước khi đặt giá.");
        }

        if (!userDao.existsByUsername(bidderUsername)) {
            return new AuctionProductResponse(false, "Không tìm thấy tài khoản.");
        }

        if (userDao.isAdmin(bidderUsername)) {
            return new AuctionProductResponse(false, "Admin không thể đặt giá.");
        }

        String selectSql = """
                SELECT current_price, buy_now_price, end_time, status, highest_bidder_username, seller_username,
                       admin_commission_paid, admin_commission_amount
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
                SET current_price = ?, status = ?, highest_bidder_username = ?, end_time = ?,
                    admin_commission_paid = ?, admin_commission_amount = ?
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

                    int currentPrice = rs.getInt("current_price");
                    int buyNowPrice = rs.getInt("buy_now_price");
                    String sellerUsername = rs.getString("seller_username");
                    String previousHighestBidderUsername = normalizeUsername(rs.getString("highest_bidder_username"));
                    boolean adminCommissionPaid = rs.getBoolean("admin_commission_paid");
                    long adminCommissionAmount = rs.getLong("admin_commission_amount");

                    LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                    String currentStatus = resolveStatus(endTime, rs.getString("status"));
                    if (!"OPEN".equals(currentStatus)) {
                        finishProduct(connection, productId, currentPrice, previousHighestBidderUsername,
                                adminCommissionPaid, adminCommissionAmount);
                        connection.commit();
                        return new AuctionProductResponse(false, "Phiên đấu giá đã kết thúc.");
                    }

                    if (bidderUsername.equals(sellerUsername)) {
                        connection.rollback();
                        return new AuctionProductResponse(false, "Không thể đặt giá cho sản phẩm của chính bạn.");
                    }

                    if (bidderUsername.equals(previousHighestBidderUsername)) {
                        connection.rollback();
                        return new AuctionProductResponse(false, "Bạn đang là người trả cao nhất");
                    }

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
                    LocalDateTime nextEndTime = calculateNextEndTime(endTime, bidTime, nextStatus);

                    if (latestBidTime != null
                            && Duration.between(latestBidTime, bidTime).toMillis() < MIN_BID_INTERVAL_MILLIS) {
                        connection.rollback();
                        return new AuctionProductResponse(false, "Vui lòng chờ 100ms trước khi đặt giá tiếp theo.");
                    }

                    Long highestBidderBalance = userDao.deductBalance(connection, bidderUsername, nextPrice);
                    if (highestBidderBalance == null) {
                        connection.rollback();
                        return new AuctionProductResponse(false, "Số dư không đủ để đặt giá này.");
                    }

                    Long previousHighestBidderBalance = null;
                    if (previousHighestBidderUsername != null) {
                        previousHighestBidderBalance = userDao.addBalance(connection, previousHighestBidderUsername, currentPrice);
                        if (previousHighestBidderBalance == null) {
                            connection.rollback();
                            return new AuctionProductResponse(false, "Không thể hoàn tiền cho người bị vượt giá.");
                        }
                    }

                    long nextAdminCommissionAmount = 0L;
                    boolean nextAdminCommissionPaid = false;
                    if ("FINISHED".equals(nextStatus)) {
                        nextAdminCommissionAmount = calculateAdminCommission(nextPrice);
                        if (nextAdminCommissionAmount > 0L
                                && userDao.addBalance(connection, UserDao.ADMIN_USERNAME, nextAdminCommissionAmount) == null) {
                            connection.rollback();
                            return new AuctionProductResponse(false, "Không thể chuyển phí cho admin.");
                        }
                        nextAdminCommissionPaid = true;
                    }

                    updateStatement.setInt(1, nextPrice);
                    updateStatement.setString(2, nextStatus);
                    updateStatement.setString(3, bidderUsername);
                    updateStatement.setTimestamp(4, Timestamp.valueOf(nextEndTime));
                    updateStatement.setBoolean(5, nextAdminCommissionPaid);
                    updateStatement.setLong(6, nextAdminCommissionAmount);
                    updateStatement.setInt(7, productId);

                    if (updateStatement.executeUpdate() <= 0) {
                        connection.rollback();
                        return new AuctionProductResponse(false, "Không thể cập nhật giá sản phẩm.");
                    }

                    insertBidStatement.setInt(1, productId);
                    insertBidStatement.setInt(2, nextPrice);
                    insertBidStatement.setTimestamp(3, Timestamp.valueOf(bidTime));
                    insertBidStatement.executeUpdate();

                    connection.commit();

                    AuctionProductResponse response = new AuctionProductResponse(
                            true,
                            "FINISHED".equals(nextStatus)
                                    ? "Đặt giá thành công. Sản phẩm đã đạt giá chốt."
                                    : "Đặt giá thành công.",
                            nextPrice,
                            nextStatus,
                            bidTime.format(DATE_TIME_FORMATTER)
                    );
                    response.setNewBalance(highestBidderBalance);
                    response.setHighestBidderUsername(bidderUsername);
                    response.setPreviousHighestBidderUsername(previousHighestBidderUsername);
                    response.setHighestBidderBalance(highestBidderBalance);
                    response.setPreviousHighestBidderBalance(previousHighestBidderBalance);
                    response.setEndTime(nextEndTime.format(DATE_TIME_FORMATTER));
                    return response;
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

    public AuctionProductResponse removeProduct(int productId, String adminUsername) {
        if (!ensureTablesExist()) {
            return new AuctionProductResponse(false, "Không thể truy cập dữ liệu sản phẩm.");
        }

        if (adminUsername == null || adminUsername.isBlank() || !userDao.isAdmin(adminUsername)) {
            return new AuctionProductResponse(false, "Chỉ admin mới được xóa sản phẩm.");
        }

        String selectSql = """
                SELECT current_price, highest_bidder_username, admin_commission_paid, admin_commission_amount
                FROM auction_products
                WHERE id = ?
                FOR UPDATE
                """;
        String deleteBidsSql = "DELETE FROM auction_product_bids WHERE product_id = ?";
        String deleteProductSql = "DELETE FROM auction_products WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                 PreparedStatement deleteBidsStatement = connection.prepareStatement(deleteBidsSql);
                 PreparedStatement deleteProductStatement = connection.prepareStatement(deleteProductSql)) {
                selectStatement.setInt(1, productId);

                try (ResultSet rs = selectStatement.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return new AuctionProductResponse(false, "Không tìm thấy sản phẩm.");
                    }

                    int currentPrice = rs.getInt("current_price");
                    String highestBidderUsername = normalizeUsername(rs.getString("highest_bidder_username"));
                    boolean adminCommissionPaid = rs.getBoolean("admin_commission_paid");
                    long adminCommissionAmount = rs.getLong("admin_commission_amount");

                    Long refundedBidderBalance = null;
                    if (highestBidderUsername != null) {
                        refundedBidderBalance = userDao.addBalance(connection, highestBidderUsername, currentPrice);
                        if (refundedBidderBalance == null) {
                            connection.rollback();
                            return new AuctionProductResponse(false, "Không thể hoàn tiền cho người đang trả cao nhất.");
                        }
                    }

                    Long adminBalance = userDao.getBalance(connection, UserDao.ADMIN_USERNAME);
                    if (adminCommissionPaid && adminCommissionAmount > 0L) {
                        adminBalance = userDao.deductBalance(connection, UserDao.ADMIN_USERNAME, adminCommissionAmount);
                        if (adminBalance == null) {
                            connection.rollback();
                            return new AuctionProductResponse(false, "Không thể hoàn lại phí admin của sản phẩm này.");
                        }
                    }

                    deleteBidsStatement.setInt(1, productId);
                    deleteBidsStatement.executeUpdate();

                    deleteProductStatement.setInt(1, productId);
                    if (deleteProductStatement.executeUpdate() <= 0) {
                        connection.rollback();
                        return new AuctionProductResponse(false, "Không thể xóa sản phẩm.");
                    }

                    connection.commit();

                    AuctionProductResponse response = new AuctionProductResponse(
                            true,
                            highestBidderUsername == null
                                    ? "Đã xóa sản phẩm."
                                    : "Đã xóa sản phẩm và hoàn tiền cho người đang trả cao nhất.",
                            null,
                            "REMOVED",
                            LocalDateTime.now().format(DATE_TIME_FORMATTER)
                    );
                    response.setPreviousHighestBidderUsername(highestBidderUsername);
                    response.setPreviousHighestBidderBalance(refundedBidderBalance);
                    response.setHighestBidderUsername(UserDao.ADMIN_USERNAME);
                    response.setHighestBidderBalance(adminBalance);
                    response.setNewBalance(adminBalance);
                    return response;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuctionProductResponse(false, "Không thể xóa sản phẩm.");
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
        return ensureProductTableExists()
                && ensureHighestBidderColumnExists()
                && ensureAdminCommissionColumnsExist()
                && ensureBidHistoryTableExists()
                && userDao.ensureAdminAccountExists();
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
                    highest_bidder_username VARCHAR(255) DEFAULT NULL,
                    admin_commission_paid BOOLEAN NOT NULL DEFAULT FALSE,
                    admin_commission_amount BIGINT NOT NULL DEFAULT 0,
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

    private boolean ensureHighestBidderColumnExists() {
        return ensureProductColumnExists(
                "highest_bidder_username",
                "highest_bidder_username VARCHAR(255) DEFAULT NULL"
        );
    }

    private boolean ensureAdminCommissionColumnsExist() {
        return ensureProductColumnExists(
                "admin_commission_paid",
                "admin_commission_paid BOOLEAN NOT NULL DEFAULT FALSE"
        ) && ensureProductColumnExists(
                "admin_commission_amount",
                "admin_commission_amount BIGINT NOT NULL DEFAULT 0"
        );
    }

    private boolean ensureProductColumnExists(String columnName, String columnDefinition) {
        String checkSql = "SHOW COLUMNS FROM auction_products LIKE ?";
        String alterSql = "ALTER TABLE auction_products ADD COLUMN " + columnDefinition;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, columnName);
            try (ResultSet rs = checkStatement.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }

            try (PreparedStatement alterStatement = connection.prepareStatement(alterSql)) {
                alterStatement.execute();
                return true;
            }
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

    private void settleExpiredAuctions() {
        String selectSql = """
                SELECT id, current_price, highest_bidder_username, admin_commission_paid, admin_commission_amount
                FROM auction_products
                WHERE (end_time <= NOW() OR status = 'FINISHED')
                  AND (status <> 'FINISHED'
                       OR (highest_bidder_username IS NOT NULL AND admin_commission_paid = FALSE))
                FOR UPDATE
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(selectSql);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    finishProduct(
                            connection,
                            rs.getInt("id"),
                            rs.getInt("current_price"),
                            normalizeUsername(rs.getString("highest_bidder_username")),
                            rs.getBoolean("admin_commission_paid"),
                            rs.getLong("admin_commission_amount")
                    );
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void finishProduct(Connection connection, int productId, int currentPrice, String highestBidderUsername,
                               boolean adminCommissionPaid, long adminCommissionAmount) throws SQLException {
        long commissionAmount = adminCommissionAmount;
        boolean commissionPaid = adminCommissionPaid;

        if (highestBidderUsername != null && !commissionPaid) {
            commissionAmount = calculateAdminCommission(currentPrice);
            if (commissionAmount > 0L) {
                Long adminBalance = userDao.addBalance(connection, UserDao.ADMIN_USERNAME, commissionAmount);
                if (adminBalance == null) {
                    throw new SQLException("Could not credit admin commission");
                }
            }
            commissionPaid = true;
        }

        String sql = """
                UPDATE auction_products
                SET status = 'FINISHED', admin_commission_paid = ?, admin_commission_amount = ?
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, commissionPaid);
            statement.setLong(2, commissionAmount);
            statement.setInt(3, productId);
            statement.executeUpdate();
        }
    }

    private String resolveStatus(LocalDateTime endTime) {
        return endTime.isBefore(LocalDateTime.now()) ? "FINISHED" : "OPEN";
    }

    private String resolveStatus(LocalDateTime endTime, String storedStatus) {
        if (storedStatus != null && "FINISHED".equalsIgnoreCase(storedStatus)) {
            return "FINISHED";
        }

        return resolveStatus(endTime);
    }

    private int calculateMinimumBid(int currentPrice) {
        return (int) Math.ceil(currentPrice * 1.02d);
    }

    private long calculateAdminCommission(int price) {
        return price * ADMIN_COMMISSION_PERCENT / 100L;
    }

    private LocalDateTime calculateNextEndTime(LocalDateTime currentEndTime, LocalDateTime bidTime, String nextStatus) {
        if (!"OPEN".equals(nextStatus)) {
            return currentEndTime;
        }

        Duration remaining = Duration.between(bidTime, currentEndTime);
        if (!remaining.isNegative() && remaining.toMinutes() < ANTI_SNIPING_THRESHOLD_MINUTES) {
            return currentEndTime.plusMinutes(ANTI_SNIPING_EXTENSION_MINUTES);
        }

        return currentEndTime;
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return username;
    }
}
