package View;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

public class signUpController {
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField passMatKhau;
    @FXML private PasswordField passNhapLai;
    @FXML private TextField txtEmail;
    @FXML private TextField txtSDT;
    @FXML private ChoiceBox<String> role;

    @FXML
    public void signUp(ActionEvent event) {
        String username = txtTenDangNhap.getText();
        String password = passMatKhau.getText();
        String nhapLai = passNhapLai.getText();
        String email = txtEmail.getText();
        String sdt = txtSDT.getText();
        String userRole = role.getValue();


        if (username.isEmpty()|| password.isEmpty() || nhapLai.isEmpty()|| email.isEmpty() || sdt.isEmpty()) {
            AlertBox.display("Hãy nhập hết các ô trống");
            return;
        }

        if (!nhapLai.equalsIgnoreCase(password)) {
            AlertBox.display("Bạn nhập lại sai mật khẩu");
            return;
        }

        if (password.length() < 6) {
            AlertBox.display("Mật khẩu quá ngắn");
            return;
        }
        if (containLetter(sdt) || !containSymbol(email)){ // check xem có chữ trong sđt không và check xem email đã có "@" chưa
            AlertBox.display("SĐT hoặc email không hợp lệ");
        }

        User newUser = new User(username, password, email, sdt, userRole);
        saveToJson(newUser);
    }


    private boolean containLetter(String msg){
        if (msg.matches(".*[a-zA-Z].*")) {
            return true;
        }
        return false;
    }
    private  boolean containSymbol(String msg){
        if (!msg.contains("@")) {
            return false;
        }
        return true;
    }

    private void saveToJson(User user) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<User> userList = new ArrayList<>();
        String fileName = "database.json";

        try {
            // 1. Kiểm tra nếu file đã tồn tại thì đọc dữ liệu cũ
            if (Files.exists(Paths.get(fileName))) {
                String jsonContent = new String(Files.readAllBytes(Paths.get(fileName)));

                // Chuyển đổi JSON cũ thành List<User>
                Type listType = new TypeToken<ArrayList<User>>(){}.getType();
                List<User> existingUsers = gson.fromJson(jsonContent, listType);

                if (existingUsers != null) {
                    userList = existingUsers;
                }
            }
            // 2. Thêm người dùng mới vào danh sách
            userList.add(user);

            // 3. Ghi đè toàn bộ danh sách mới vào file (FileWriter không để 'true')
            try (FileWriter writer = new FileWriter(fileName)) {
                gson.toJson(userList, writer);
                System.out.println("Đã cập nhật danh sách người dùng vào JSON!");
            }

        } catch (IOException e) {
            System.err.println("Lỗi khi xử lý file JSON: " + e.getMessage());
        }
    }
}
