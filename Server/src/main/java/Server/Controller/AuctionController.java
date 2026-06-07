package Server.Controller;
import Server.Dto.Respone.AuctionCardRespone;
import Server.Dto.Respone.AuctionDetailRespone;
import Server.Service.AuctionService;
import Server.Service.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService  auctionService;
    private final ProfileService profileService;

    public AuctionController(AuctionService auctionService, ProfileService profileService) {
        this.auctionService = auctionService;
        this.profileService = profileService;
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/auctions/getcards
    // Trả về tất cả phiên đấu giá (không lọc theo user)
    // ─────────────────────────────────────────────────────────

    @GetMapping("/getcards")
    public List<AuctionCardRespone> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/auctions/getdetail?id={auctionId}
    // ─────────────────────────────────────────────────────────

    @GetMapping("/getdetail")
    public AuctionDetailRespone getDetail(@RequestParam String id) {
        return auctionService.getDetail(id);
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/auctions/mycards?id={userId}
    // Trả về các phiên đấu giá của/liên quan đến userId
    // ─────────────────────────────────────────────────────────

    /**
     * Lấy danh sách phiên đấu giá liên quan đến một người dùng cụ thể.
     *  - Seller  → các phiên do họ tạo ra
     *  - Bidder  → các phiên họ đã tham gia đặt giá
     *
     * @param id userId
     */
    @GetMapping("/mycards")
    public List<AuctionCardRespone> getAllMyAuctions(@RequestParam String id) {
        return profileService.getAllMyAuctions(id);
    }
}