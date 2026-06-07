package Server.Controller;

import Server.Dto.Request.LoginRequest;
import Server.Dto.Request.RegisterRequest;
import Server.Dto.Respone.AuthRespone;
import Server.Service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthRespone signup(@RequestBody RegisterRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthRespone login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
