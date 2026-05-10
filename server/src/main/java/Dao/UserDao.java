package Dao;

import Database.DatabaseConnection;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public boolean existsByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(User user) {
        String sql = """
               INSERT INTO users(username, email, password, phone) VALUES (?, ?, ?, ?)
               """;
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getPhonenumber());
            int rows = statement.executeUpdate();
            return rows > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
