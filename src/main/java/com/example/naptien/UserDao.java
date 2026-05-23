package com.example.naptien;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
public class UserDao {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/bidding_database";
        String username = "root";
        String password = "";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }

    // Hàm thực thi lệnh cộng tiền vào SQL
    public static boolean updateBalance(String username, int amount) {
        String sql = "UPDATE user SET balance = balance + ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, amount);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}