package Dao;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public static final String USER_ROLE = "User";
    public static final String ADMIN_ROLE = "Admin";
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_EMAIL = "admin@auction.local";

    public Long deposit(String username, long amount) {
        if (!ensureUsersTableExists()) {
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
        if (!ensureUsersTableExists()) {
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
        if (username == null || username.isBlank() || !ensureUsersTableExists()) {
            return false;
        }

        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String email, String password, String role) {
        if (!ensureUsersTableExists()) {
            return false;
        }

        String sql = """
               INSERT INTO users(username, email, password, role) VALUES (?, ?, ?, ?)
               """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, normalizePublicRole(role));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String findRoleByCredentials(String username, String password) {
        if (!ensureUsersTableExists()) {
            return null;
        }

        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return normalizeStoredRole(rs.getString("role"));
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String findRoleByUsername(String username) {
        if (username == null || username.isBlank() || !ensureUsersTableExists()) {
            return null;
        }

        String sql = "SELECT role FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return normalizeStoredRole(rs.getString("role"));
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAdmin(String username) {
        return ADMIN_ROLE.equals(findRoleByUsername(username));
    }

    public boolean ensureAdminAccountExists() {
        if (!ensureUsersTableExists()) {
            return false;
        }

        String selectSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = """
                INSERT INTO users(username, email, password, role, balance)
                VALUES (?, ?, ?, ?, 0)
                """;
        String updateSql = """
                UPDATE users
                SET email = ?, password = ?, role = ?
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            selectStatement.setString(1, ADMIN_USERNAME);
            try (ResultSet rs = selectStatement.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                        updateStatement.setString(1, ADMIN_EMAIL);
                        updateStatement.setString(2, ADMIN_PASSWORD);
                        updateStatement.setString(3, ADMIN_ROLE);
                        updateStatement.setString(4, ADMIN_USERNAME);
                        return updateStatement.executeUpdate() > 0;
                    }
                }
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setString(1, ADMIN_USERNAME);
                insertStatement.setString(2, ADMIN_EMAIL);
                insertStatement.setString(3, ADMIN_PASSWORD);
                insertStatement.setString(4, ADMIN_ROLE);
                return insertStatement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean ensureUsersTableExists() {
        return ensureUsersTableCreated()
                && ensureRoleColumnExists()
                && ensureBalanceColumnExists()
                && normalizeSellerRoles();
    }

    public boolean ensureBalanceColumnExists() {
        if (!ensureUsersTableCreated()) {
            return false;
        }

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

    private boolean ensureUsersTableCreated() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    email VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(50) NOT NULL DEFAULT 'User',
                    balance BIGINT NOT NULL DEFAULT 0
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

    private boolean ensureRoleColumnExists() {
        String checkSql = "SHOW COLUMNS FROM users LIKE 'role'";
        String alterSql = "ALTER TABLE users ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'User'";

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

    private boolean normalizeSellerRoles() {
        String sql = """
                UPDATE users
                SET role = ?
                WHERE LOWER(role) = 'seller'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, USER_ROLE);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String normalizePublicRole(String role) {
        return USER_ROLE;
    }

    private String normalizeStoredRole(String role) {
        if (ADMIN_ROLE.equalsIgnoreCase(role)) {
            return ADMIN_ROLE;
        }

        return USER_ROLE;
    }
}
