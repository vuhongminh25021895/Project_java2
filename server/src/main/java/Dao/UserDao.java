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

        try (Connection connection = DatabaseConnection.getConnection()) {
            return addBalance(connection, username, amount);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getBalance(String username) {
        if (!ensureBalanceColumnExists()) {
            return null;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            return getBalance(connection, username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getBalance(Connection connection, String username) throws SQLException {
        String selectSql = "SELECT COALESCE(balance, 0) AS balance FROM users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("balance");
                }
            }
        }
        return null;
    }

    public Long addBalance(Connection connection, String username, long amount) throws SQLException {
        if (username == null || username.isBlank() || amount <= 0L) {
            return null;
        }

        String updateSql = """
                UPDATE users
                SET balance = COALESCE(balance, 0) + ?
                WHERE username = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setLong(1, amount);
            statement.setString(2, username);

            if (statement.executeUpdate() <= 0) {
                return null;
            }
        }

        return getBalance(connection, username);
    }

    public Long deductBalance(Connection connection, String username, long amount) throws SQLException {
        if (username == null || username.isBlank() || amount <= 0L) {
            return null;
        }

        String updateSql = """
                UPDATE users
                SET balance = COALESCE(balance, 0) - ?
                WHERE username = ? AND COALESCE(balance, 0) >= ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setLong(1, amount);
            statement.setString(2, username);
            statement.setLong(3, amount);

            if (statement.executeUpdate() <= 0) {
                return null;
            }
        }

        return getBalance(connection, username);
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

    public boolean ensureBalanceColumnExists() {
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
