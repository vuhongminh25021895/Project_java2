package Dao;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public Long deposit(String username, long amount) {
        if (!ensureBalanceColumnExists()) {
            return null;
        }

        String updateSql = """
                UPDATE users
                SET balance = COALESCE(balance, 0) + ?
                WHERE username = ?
                """;
        String selectSql = "SELECT balance FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
            updateStatement.setLong(1, amount);
            updateStatement.setString(2, username);

            if (updateStatement.executeUpdate() <= 0) {
                return null;
            }

            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setString(1, username);
                try (ResultSet rs = selectStatement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("balance");
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String email, String password, String role) {
        String sql = """
               INSERT INTO users(username, email, password, role) VALUES (?, ?, ?, ?)
               """;
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, role);
            int rows = statement.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String findRoleByCredentials(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean ensureBalanceColumnExists() {
        String checkSql = "SHOW COLUMNS FROM users LIKE 'balance'";
        String alterSql = "ALTER TABLE users ADD COLUMN balance BIGINT NOT NULL DEFAULT 0";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkSql);
             ResultSet rs = checkStatement.executeQuery()) {
            if (rs.next()) {
                return true;
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
}
