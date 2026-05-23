package Dao;

import Database.DatabaseConnection;
import Dto.AuctionProductItem;
import Dto.AuctionProductRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionProductDao {
    public boolean create(AuctionProductRequest request) {
        if (!ensureTableExists()) {
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
        if (!ensureTableExists()) {
            return items;
        }

        String sql = """
                SELECT id, category, product_name, current_price, buy_now_price, end_time, status, seller_username
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

    private boolean ensureTableExists() {
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

    private String resolveStatus(LocalDateTime endTime) {
        return endTime.isBefore(LocalDateTime.now()) ? "FINISHED" : "OPEN";
    }
}
