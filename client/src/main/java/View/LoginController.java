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

        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = loginService.login(request);

        if (response.isSuccess()) {
            try {
                AuctionListController.setCurrentUsername(username);
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
