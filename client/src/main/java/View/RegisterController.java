package View;


import Dto.Request.RegisterRequest;
import Dto.Respone.AuthRespone;
import Scene.SceneManager;
import Scene.SceneName;
import Service.AuthService;
import Session.ClientSession;
import Util.AlertBox;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;


public class RegisterController {
    private final AuthService authService = new AuthService();
    private ClientSession clientSession = ClientSession.getInstance();

    @FXML private TextField txtFullName;
    @FXML private TextField txtTenDangNhap;
    @FXML private TextField txtEmail;
    @FXML private PasswordField passMatKhau;
    @FXML private PasswordField passNhapLai;
    @FXML private ChoiceBox<String> role;
    @FXML private Label requirement;
    @FXML private Label lblMessage;

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

    private boolean containsAtSign(String email) {
        return email.contains("@");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6
                && password.matches(".*[a-zA-Z].*")
                && password.matches(".*\\d.*");
    }

    @FXML
    public void Loginswitch() {
        SceneManager.switchTo(SceneName.LOGIN);
    }

    @FXML
    public void register(ActionEvent event) {
        String fullname = txtFullName.getText().trim();
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

        RegisterRequest registerRequest = new RegisterRequest(fullname, username, email, userRole, password);
        AuthRespone registerRespone = authService.signup(registerRequest);

        if (registerRespone.success()) {
            lblMessage.setText(registerRespone.message());
            lblMessage.setVisible(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> {
                SceneManager.switchTo(SceneName.LOGIN);
            });
            pause.play();
        }
    }
=======
public class RegisterController {

}
