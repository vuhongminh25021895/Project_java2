package app.client.controller;

import app.shared.dto.response.AuthResponse;
import app.shared.dto.request.LoginRequest;
import app.shared.dto.request.SignupRequest;

import app.client.service.AuthService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthResponse signup(
            @RequestBody SignupRequest request
    ) {

        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {

        return authService.login(request);
    }
}
