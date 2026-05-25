package View;

import Dto.RegisterRequest;
import Dto.RegisterRespone;
import Service.RegisterService;
import StageManager.SceneName;
import StageManager.SwitchSceneControll;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
    private final RegisterService registerService = new RegisterService();

    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField passMatKhau;
    @FXML private PasswordField passNhapLai;
    @FXML private TextField txtEmail;
    @FXML private ChoiceBox<String> role;
    @FXML private Label requirement;

    @FXML
    public void initialize() {
        requirement.setVisible(false);
        requirement.setManaged(false);
        requirement.setStyle("-fx-text-fill: #ff0000; -fx-font-size: 11px;");

        passMatKhau.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                requirement.setVisible(true);
                requirement.setManaged(true);
            } else if (isValidPassword(passMatKhau.getText())) {
                requirement.setVisible(false);
                requirement.setManaged(false);
            }
        });

        passMatKhau.textProperty().addListener((obs, oldText, newText) -> {
            if (isValidPassword(newText)) {
                requirement.setStyle("-fx-text-fill: #008000;");
                requirement.setText("✔ Password meets requirements");
            } else {
                requirement.setStyle("-fx-text-fill: #ff0000;");
                requirement.setText("• At least 6 chars, 1 letter, 1 number");
            }
        });
    }

    @FXML
    public void MainSceneSwitch(ActionEvent event) {
        try {
            SwitchSceneControll.switchScene(SceneName.LOGIN);
        } catch (Exception e) {
            AlertBox.display("Không thể chuyển về màn hình đăng nhập.");
        }
    }

    @FXML
    public void signUp(ActionEvent event) {
        String username = txtTenDangNhap.getText().trim();
        String password = passMatKhau.getText().trim();
        String confirmPassword = passNhapLai.getText().trim();
        String email = txtEmail.getText().trim();
        String userRole = role.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            AlertBox.display("Hãy nhập hết các ô trống");
            return;
        }

        if (!confirmPassword.equals(password)) {
            AlertBox.display("Bạn nhập lại sai mật khẩu");
            return;
        }

        if (!isValidPassword(password)) {
            AlertBox.display("Mật khẩu phải có ít nhất 6 ký tự, gồm chữ và số");
            return;
        }

        if (!containsAtSign(email)) {
            AlertBox.display("Email không hợp lệ");
            return;
        }

        RegisterRequest request = registerService.registerRequest(username, email, password, userRole);
        RegisterRespone response = registerService.register(request);

        if (response.isSuccess()) {
            AlertBox.display("Đăng ký thành công");
            try {
                SwitchSceneControll.switchScene(SceneName.LOGIN);
            } catch (Exception e) {
                AlertBox.display("Đăng ký thành công nhưng không thể chuyển màn hình.");
            }
            return;
        }

        AlertBox.display(response.getMessage());
    }

    private boolean containsAtSign(String email) {
        return email.contains("@");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6
                && password.matches(".*[a-zA-Z].*")
                && password.matches(".*\\d.*");
    }
}
