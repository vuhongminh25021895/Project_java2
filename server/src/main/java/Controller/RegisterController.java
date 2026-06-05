package Controller;

import Dao.UserDao;
import Dto.LoginRequest;
import Dto.LoginResponse;
import Dto.RegisterRequest;
import Dto.RegisterResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {
    private final UserDao userDao;

    public RegisterController() {
        userDao = new UserDao();
        userDao.ensureAdminAccountExists();
    }

    @PostMapping("/register")
    public RegisterResponse registerResponse(@RequestBody RegisterRequest request) {
        try {
            if (request.getUsername() == null || request.getEmail() == null || request.getPassword() == null
                    || request.getUsername().isBlank() || request.getEmail().isBlank() || request.getPassword().isBlank()) {
                return new RegisterResponse(false, "Lỗi thông tin gửi");
            }

            if (userDao.existsByUsername(request.getUsername())) {
                return new RegisterResponse(false, "Tên đăng nhập đã tồn tại!");
            }

            boolean success = userDao.register(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    UserDao.USER_ROLE
            );

            if (success) {
                return new RegisterResponse(true, "Register success");
            }
            return new RegisterResponse(false, "Register failed");
        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterResponse(false, "Server error");
        }
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            if (request.getUsername() == null || request.getPassword() == null
                    || request.getUsername().isBlank() || request.getPassword().isBlank()) {
                return new LoginResponse(false, "Hãy nhập tên đăng nhập và mật khẩu", null);
            }

            String role = userDao.findRoleByCredentials(request.getUsername(), request.getPassword());
            if (role == null) {
                return new LoginResponse(false, "Tên đăng nhập hoặc mật khẩu không chính xác!", null);
            }

            return new LoginResponse(true, "Login success", role);
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(false, "Server error", null);
        }
    }
}
