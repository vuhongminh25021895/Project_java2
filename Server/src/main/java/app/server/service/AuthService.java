package app.server.service;

import app.server.dto.request.LoginRequest;
import app.server.dto.request.SignupRequest;
import app.server.dto.response.AuthResponse;
import app.server.factory.UserFactory;
import app.server.model.User;
import app.server.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(
            UserRepository userRepository
    ) {

        this.userRepository = userRepository;
    }

    public AuthResponse signup(
            SignupRequest request
    ) {

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

        User user = UserFactory.createUser(
                request.getRole(),
                request.getUsername(),
                request.getPassword(),
                request.getFullName()
        );

        userRepository.save(user);

        return new AuthResponse(
                true,
                "Signup success",
                user.getId()
        );
    }

    public AuthResponse login(
            LoginRequest request
    ) {

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