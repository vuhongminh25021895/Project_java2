package Server.Service;


import Server.Dto.Request.LoginRequest;
import Server.Dto.Request.RegisterRequest;
import Server.Dto.Respone.AuthRespone;
import Server.Enums.UserRole;
import Server.Model.Bidder;
import Server.Model.Seller;
import Server.Model.User;
import Server.Repository.UserRepository;
import Server.Security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {

        this.userRepository = userRepository;
        this.jwtService= jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthRespone login(LoginRequest request) {
        if (userRepository.existsByUserName(request.username())) {
            return new AuthRespone(false, "Username already exists", null, null, null, null);
        }
        User user = userRepository.findByUserName(request.username());
        if (user == null || !passwordEncoder.matches(request.password(), passwordEncoder.encode(user.getPassword()))) {
            return new AuthRespone(false, "Invalid username or password", null, null, null, null);
        }
        String token = jwtService.createToken(user.getId(), user.getRole());
        return new AuthRespone(true, "Login successful", token, user.getId(), user.getUserName(), user.getRole().name());
    }

    @Transactional
    public AuthRespone signup(RegisterRequest request) {
        if (userRepository.existsByUserName(request.username())) {
            return new AuthRespone(false, "Username already exists", null, null, null, null);
        }
        if (userRepository.existsByEmail(request.email())) {
            return new AuthRespone(false, "Email already registered", null, null, null, null);
        }
        User user = null;
        String hashpass = passwordEncoder.encode(request.password());
        switch (request.userRole().toUpperCase()) {
            case "SELLER" -> user = Seller.builder()
                    .fullName(request.fullname())
                    .userName(request.username())
                    .password(hashpass)
                    .email(request.email())
                    .role(UserRole.SELLER)
                    .balance(BigDecimal.ZERO)
                    .build();

            case "BIDDER" -> user = Bidder.builder()
                    .fullName(request.fullname())
                    .userName(request.username())
                    .password(hashpass)
                    .email(request.email())
                    .role(UserRole.BIDDER)
                    .balance(BigDecimal.ZERO)
                    .build();
        }
        user = userRepository.save(user);
        return new AuthRespone(true, "Signup success", null, user.getId(), user.getUserName(), user.getRole().toString());
    }
}
