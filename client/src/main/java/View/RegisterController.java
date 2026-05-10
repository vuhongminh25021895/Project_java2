package View;
import Dto.RegisterRequest;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Service.RegisterService;

import java.io.IOException;

public class RegisterController {

    RegisterService registerservice = new RegisterService();
    @FXML private TextField txttendangnhap;
    @FXML private PasswordField txtpassword;
    @FXML private PasswordField txtre_enterpasssword;
    @FXML private TextField txtemail;
    @FXML private TextField txtphonenumber;
    @FXML private Label notic;
    @FXML private Button signupbutton;

    @FXML
    public void initialize() {
        txttendangnhap.textProperty().addListener((obs, odlVal, newVal) -> {
            validateUsername();
            updateRegisterButtonState();
        });
        txtpassword.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePassword();
            updateRegisterButtonState();
        });
        txtre_enterpasssword.textProperty().addListener((obs, oldVal, newVal) -> {
            validateConfrimPassword();
            updateRegisterButtonState();
        });
        txtemail.textProperty().addListener((obs, oldVal, newVal) -> {
            validateEmail();
            updateRegisterButtonState();
        });
        txtphonenumber.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePhoneNumber();
            updateRegisterButtonState();
        });
    }

    private boolean validateUsername() {
        String tendangnhap = txttendangnhap.getText().trim();
        if (tendangnhap.isEmpty()) {
            notic.setText("Yeu cau nhap ten dang nhap");
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String email = txtemail.getText().trim();
        if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            notic.setText("Email loi dinh dang");
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        String password = txtpassword.getText().trim();
        if (isValidPassword(password)) {
            notic.setText("Password khong du yeu cau");
            return false;
        }
        return true;
    }

    private boolean validateConfrimPassword() {
        String password = txtpassword.getText().trim();
        String confirm = txtre_enterpasssword.getText().trim();
        if (!confirm.equals(password)) {
            notic.setText("Password khong dung");
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber() {
        String phonenumber = txtphonenumber.getText().trim();
        if (phonenumber.matches("^(0)[0-9]{9}$")) {
            notic.setText("So dien thoai khong dung");
            return false;
        }
        return true;
    }


    private boolean isFormValid() {
        return validateUsername() && validateEmail() && validatePassword() && validateConfrimPassword() && validatePhoneNumber();
    }

    private void updateRegisterButtonState() {
        signupbutton.setDisable(!isFormValid());
        if (isFormValid()) {
            notic.setText("");
        }
    }

    private boolean isValidPassword(String password){
        return password.length() >= 6 &&
                password.matches(".*[a-zA-Z].*") &&
                password.matches(".*\\d.*");
    }

    @FXML
    private void handleRegister() throws IOException {
        if (!isFormValid()) {
            return;
        }
        signupbutton.setDisable(true);

        String username = txttendangnhap.getText().trim();
        String email = txtemail.getText().trim();
        String password = txtpassword.getText().trim();
        String phonenumber = txtphonenumber.getText().trim();

        RegisterRequest newrequest = registerservice.registerRequest(username, email, password, phonenumber);
        registerservice.register(newrequest);

    }
}

