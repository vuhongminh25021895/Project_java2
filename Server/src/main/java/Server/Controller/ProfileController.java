package Server.Controller;


import Server.Dto.Respone.ProfileRespone;
import Server.Service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/profile
    // ─────────────────────────────────────────────────────────

    /**
     * Trả về thông tin tổng hợp của người dùng hiện tại.
     *
     * userId được lấy từ JWT (SecurityContext), không cần truyền qua URL.
     * Trả về ProfileResponse bao gồm:
     *   - Thông tin cơ bản: fullname, username, email, createAt
     *   - Thống kê: participatedAuctions, wonAuctions, revenue, productscount
     */
    @GetMapping
    public ProfileRespone getProfile() {
        return profileService.getProfile();
    }
}
