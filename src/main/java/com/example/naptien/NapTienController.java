package com.example.naptien;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NapTienController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField txtSoTien;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bidđing_database";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public boolean isDataValid() {
        String cash = txtSoTien.getText();

        if (cash == null || cash.trim().isEmpty()) {
            AlertBox.display("Vui lòng không để trống số tiền!");
            return false;
        }

        if (cash.chars().anyMatch(ch -> !Character.isDigit(ch))) {
            AlertBox.display("Không được chứa chữ cái hay ký tự đặc biệt!");
            return false;
        }
        try {
            int cash1 = Integer.parseInt(cash);
            if (cash1 <= 0) {
                AlertBox.display("Số tiền phải lớn hơn 0");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertBox.display("Dữ liệu không hợp lệ (Số quá lớn)!");
            return false;
        }
        return true;
    }
    public void bt1() { updateAmount(100000); }
    public void bt2() { updateAmount(1000000); }
    public void bt3() { updateAmount(10000000); }
    public void bt4() { updateAmount(100000000); }
    private void updateAmount(int amountToAdd) {
        String currentText = txtSoTien.getText();
        int currentCash = (currentText == null || currentText.isEmpty() || !currentText.matches("\\d+"))
                ? 0 : Integer.parseInt(currentText);
        txtSoTien.setText(String.valueOf(currentCash + amountToAdd));
    }
    @FXML
    public void handleButtonNap() {
        if (!isDataValid()) {
            return;
        }
        String cash = txtSoTien.getText();
        int soTienNap = Integer.parseInt(cash);
        String username = "testuser";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                String checkSql = "SELECT balance FROM user WHERE username = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        int currentBalance = rs.getInt("balance");
                        int newBalance = currentBalance + soTienNap;

                        String updateSql = "UPDATE user SET balance = ? WHERE username = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, newBalance);
                            updateStmt.setString(2, username);
                            updateStmt.executeUpdate();

                            AlertBox.display("Nạp tiền thành công trực tiếp vào XAMPP!");
                        }
                    } else {
                        AlertBox.display("Lỗi: Không tìm thấy tài khoản '" + username + "' trong DB!");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            AlertBox.display("Thiếu thư viện Driver MySQL! Hãy nhấn Reload Maven Project.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertBox.display("Lỗi kết nối MySQL! Hãy chắc chắn đã bật MySQL trên XAMPP.");
        }
    }
}

