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
        cash = cash.trim().toUpperCase();
        if (!cash.matches("\\d+[KM]?")) {
            AlertBox.display("Định dạng không hợp lệ! Chỉ nhập số hoặc số kèm K/M (Ví dụ: 500K, 2M)");
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
        int currentCash = 0;
        if (currentText != null && !currentText.trim().isEmpty()) {
            currentText = currentText.trim().toUpperCase();

            if (currentText.matches("\\d+[KM]?")) {
                currentCash = parseKMAmount(currentText);
            }
        }
        txtSoTien.setText(String.valueOf(currentCash + amountToAdd));
    }
    private int parseKMAmount(String input) {
        input = input.trim().toUpperCase();

        if (input.endsWith("K")) {
            String numberPart = input.substring(0, input.length() - 1);
            return Integer.parseInt(numberPart) * 1000;
        } else if (input.endsWith("M")) {
            String numberPart = input.substring(0, input.length() - 1);
            return Integer.parseInt(numberPart) * 1000000;
        } else {
            return Integer.parseInt(input);
        }
    }

    @FXML
    public void handleButtonNap() {
        if (!isDataValid()) {
            return;
        }
        String cashText = txtSoTien.getText();
        String username = "testuser";
        try {
            int soTienNap = parseKMAmount(cashText);

            if (soTienNap <= 0) {
                AlertBox.display("Số tiền phải lớn hơn 0");
                return;
            }

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

                            AlertBox.display("Nạp thành công " + soTienNap + " VNĐ vào XAMPP!");
                        }
                    } else {
                        AlertBox.display("Lỗi: Không tìm thấy tài khoản '" + username + "' trong DB!");
                    }
                }
            }
        } catch (NumberFormatException e) {
            AlertBox.display("Số tiền vượt quá giới hạn xử lý hoặc sai cấu trúc số!");
        } catch (ClassNotFoundException e) {
            AlertBox.display("Thiếu thư viện Driver MySQL! Hãy nhấn Reload Maven Project.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertBox.display("Lỗi kết nối MySQL! Hãy chắc chắn đã bật MySQL trên XAMPP.");
        }
    }
}
