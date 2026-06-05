package View;

import Dto.LoginRequest;
import Dto.LoginResponse;
import Service.LoginService;
import StageManager.SceneName;
import StageManager.SwitchSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private final LoginService loginService = new LoginService();

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @FXML
    public void handleSignIn(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertBox.display("Hãy nhập tên đăng nhập và mật khẩu");
            return;
        }

        signIn(username, password);
    }

    @FXML
    public void handleAdminSignIn(ActionEvent event) {
        txtUsername.setText(ADMIN_USERNAME);
        txtPassword.setText(ADMIN_PASSWORD);
        signIn(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    private void signIn(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = loginService.login(request);

        if (response.isSuccess()) {
            try {
                AuctionListController.setCurrentUsername(username);
                AuctionListController.setCurrentRole(response.getRole());
                AuctionListController.setCurrentBalance(0L);
                SwitchSceneController.switchScene(SceneName.AUCTION_LIST);
            } catch (Exception e) {
                AlertBox.display("Đăng nhập thành công nhưng không thể mở danh sách đấu giá.");
            }
            return;
        }

        AlertBox.display(response.getMessage());
    }

    @FXML
    public void SwitchToSignUp(ActionEvent event) {
        try {
            SwitchSceneController.switchScene(SceneName.REGISTER);
        } catch (Exception e) {
            AlertBox.display("Không thể mở màn hình đăng ký.");
        }
    }
}
