package Server.Service;


import Server.Dto.Request.DepositRequest;
import Server.Exception.UserNotFoundException;
import Server.Model.User;
import Server.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    private final UserRepository userRepository;

    public WalletService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deposit(BigDecimal amount) {
        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findById(userId).get();
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }
}
