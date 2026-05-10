package Controller;

import Dto.RegisterRequest;
import Dto.RegisterRespone;
import Model.User;
import org.springframework.web.bind.annotation.*;
import Dao.UserDao;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {
    private final UserDao userDao;

    public RegisterController() {
        userDao = new UserDao();
    }

    @PostMapping("/register")
    public RegisterRespone registerRespone(@RequestBody RegisterRequest request) {
        try {
            if (request.getUsername().isEmpty() || request.getEmail().isEmpty() || request.getPassword().isEmpty() || request.getPhone().isEmpty()) {
                return new RegisterRespone(false, "Loi thong tin gui");
            }
            if (userDao.existsByEmail(request.getEmail())) {
                return new RegisterRespone(false, "Email da ton tai");
            }
            User user = new User(request.getUsername(), request.getEmail(), request.getPassword(), request.getPhone());
            boolean success = userDao.register(user);

            if (success) {
                return new RegisterRespone(true, "Register success");
            }
            return new RegisterRespone(false, "Register failed");

        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterRespone(false, "Server error");
        }
    }
}

