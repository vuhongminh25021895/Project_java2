package Controller;

import Dao.UserDao;
import Dto.DepositRequest;
import Dto.DepositResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final UserDao userDao;

    public WalletController() {
        userDao = new UserDao();
    }

    @GetMapping("/{username}")
    public DepositResponse getBalance(@PathVariable String username) {
        try {
            if (username == null || username.isBlank()) {
                return new DepositResponse(false, "Thiếu tên người dùng.", null);
            }

            Long balance = userDao.getBalance(username);
            if (balance == null) {
                return new DepositResponse(false, "Không tìm thấy tài khoản hoặc không thể tải số dư.", null);
            }

            return new DepositResponse(true, "Lấy số dư thành công.", balance);
        } catch (Exception e) {
            e.printStackTrace();
            return new DepositResponse(false, "Server error", null);
        }
    }

    @PostMapping("/deposit")
    public DepositResponse deposit(@RequestBody DepositRequest request) {
        try {
            if (request == null
                    || request.getUsername() == null
                    || request.getUsername().isBlank()
                    || request.getAmount() == null) {
                return new DepositResponse(false, "Thiếu thông tin nạp tiền.", null);
            }

            long amount = request.getAmount();
            if (amount <= 0L) {
                return new DepositResponse(false, "Số tiền nạp phải lớn hơn 0.", null);
            }

            Long newBalance = userDao.deposit(request.getUsername(), amount);
            if (newBalance == null) {
                return new DepositResponse(false, "Không tìm thấy tài khoản hoặc không thể cập nhật số dư.", null);
            }

            return new DepositResponse(true, "Nạp tiền thành công.", newBalance);
        } catch (Exception e) {
            e.printStackTrace();
            return new DepositResponse(false, "Server error", null);
        }
    }
}
