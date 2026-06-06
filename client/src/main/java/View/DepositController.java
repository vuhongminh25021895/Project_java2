package View;

import Dto.Request.DepositRequest;
import Dto.Respone.DepositRespone;
import Scene.SceneManager;
import Scene.SceneName;
import Service.DepositService;
import Session.ClientSession;
import Util.AlertBox;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Locale;

public class DepositController {
    private static final Locale VIETNAM = Locale.forLanguageTag("vi-VN");
    private final DepositService depositService = new DepositService();

    @FXML private Label lblCurrentUser;
    @FXML private Label lblStatus;
    @FXML private TextField txtSoTien;
    private long amount;

    @FXML
    public void initialize() {
        String username = ClientSession.getInstance().getUsername();
        lblCurrentUser.setText(username == null || username.isBlank()
                ? "Người dùng hiện tại: Guest"
                : "Người dùng hiện tại: " + username);
        lblStatus.setText("");
    }

    @FXML
    public void bt100K() {
        updateAmount(100_000L);
    }

    @FXML
    public void bt1M() {
        updateAmount(1_000_000L);
    }

    @FXML
    public void bt10M() {
        updateAmount(10_000_000L);
    }

    @FXML
    public void bt100M() {
        updateAmount(100_000_000L);
    }

    @FXML
    public void bt1B() {
        updateAmount(1_000_000_000L);
    }

    @FXML
    private void handleButtonNap() {
        String jwtToken = ClientSession.getInstance().getJwtToken();
        if (jwtToken == null) {
            AlertBox.display("Phiên đăng nhập đã hết hạn");
            return;
        }
        try {
            amount = parseAmount(txtSoTien.getText());
        } catch (IllegalArgumentException e) {
            AlertBox.display(e.getMessage());
            return;
        }

        DepositRequest request = new DepositRequest(amount);
        DepositRespone respone = depositService.deposit(request);
        if (respone.success() == true) {
            txtSoTien.clear();
            String message = "Số dư mới" + respone.newBalance() + "VND";
            lblStatus.setText("Success");
            AlertBox.display(message);
            return;
        }
    }

    @FXML
    public void backToAuctionList() {
        SceneManager.switchTo(SceneName.AUCTION_LIST);
    }

    private void updateAmount(long amountToAdd) {
        long currentAmount = 0L;
        String currentText = txtSoTien.getText();
        if (currentText != null && !currentText.isBlank()) {
            try {
                currentAmount = parseAmount(currentText);
            } catch (IllegalArgumentException ignored) {
                currentAmount = 0L;
            }
        }

        try {
            txtSoTien.setText(String.valueOf(Math.addExact(currentAmount, amountToAdd)));
        } catch (ArithmeticException e) {
            AlertBox.display("Số tiền vượt quá giới hạn xử lý.");
        }
    }

    private long parseAmount(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập số tiền muốn nạp.");
        }

        String normalized = input.trim().replace(",", "").replace("_", "").toUpperCase();
        if (!normalized.matches("\\d+[KMB]?")) {
            throw new IllegalArgumentException("Chỉ nhập số hoặc số kèm K, M, B. Ví dụ: 500K, 2M, 1B.");
        }

        long multiplier = 1L;
        char lastChar = normalized.charAt(normalized.length() - 1);
        if (Character.isLetter(lastChar)) {
            normalized = normalized.substring(0, normalized.length() - 1);
            switch (lastChar) {
                case 'K':
                    multiplier = 1_000L;
                    break;
                case 'M':
                    multiplier = 1_000_000L;
                    break;
                case 'B':
                    multiplier = 1_000_000_000L;
                    break;
                default:
                    throw new IllegalArgumentException("Đơn vị số tiền không hợp lệ.");
            }
        }

        long amount;
        try {
            amount = Long.parseLong(normalized);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Số tiền vượt quá giới hạn xử lý.");
        }

        try {
            amount = Math.multiplyExact(amount, multiplier);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Số tiền vượt quá giới hạn xử lý.");
        }

        if (amount <= 0L) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        }

        return amount;
    }
}
