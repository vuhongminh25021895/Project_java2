package View;

import Dto.Request.LoginRequest;
import Dto.Response.AuthResponse;
import Scene.SceneManager;
import Scene.SceneName;
import Service.AuthService;
import Session.ClientSession;
import Util.AlertBox;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;

import java.util.HashMap;
import java.util.Map;

public class LoginController {
    private final AuthService authService = new AuthService();
    private ClientSession clientSession = ClientSession.getInstance();

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    private String username;
    @FXML
    public void handleLogIn(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertBox.display("Hãy nhập tên đăng nhập và mật khẩu");
            return;
        }

        LoginRequest request = new LoginRequest(username, password);
        AuthResponse loginRespone = authService.login(request);
        if (loginRespone.success()) {
            clientSession.login(
                    loginRespone.token(),
                    loginRespone.userId(),
                    loginRespone.username(),
                    loginRespone.role()
            );
            SceneManager.switchTo(SceneName.AUCTION_LIST);
        } else {
            AlertBox.display(loginRespone.message());
        }
    }

    @FXML
    public void SwitchToSignUp(ActionEvent event) {
            SceneManager.switchTo(SceneName.REGISTER);
    }
}
