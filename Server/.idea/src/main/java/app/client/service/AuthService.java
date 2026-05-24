package app.client.service;

import app.shared.dto.response.AuthResponse;
import app.shared.dto.request.LoginRequest;
import app.shared.dto.request.SignupRequest;
import app.server.model.Bidder;
import app.server.model.User;

import org.springframework.stereotype.Service;
import app.server.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse signup(SignupRequest request) {
        User existing =
                userRepository.findByUserName(
                        request.getUsername()
                );

        if (existing != null) {
            return new AuthResponse(
                    false,
                    "Username already exists",
                    null
            );
        }

        Bidder bidder = new Bidder(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFullName()
        );

        userRepository.save(bidder);

        return new AuthResponse(
                true,
                "Signup success",
                bidder.getId()
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user =
                userRepository.findByUserName(
                        request.getUsername()
                );

        if (user == null) {
            return new AuthResponse(
                    false,
                    "User not found",
                    null
            );
        }

        if (!user.getPassword().equals(
                request.getPassword()
        )) {
            return new AuthResponse(
                    false,
                    "Wrong password",
                    null
            );
        }

        return new AuthResponse(
                true,
                "Login success",
                user.getId()
        );
    }
}
