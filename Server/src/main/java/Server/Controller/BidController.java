package Server.Controller;

import Server.Dto.Request.BidRequest;
import Server.Dto.Respone.BidHistoryRespone;
import Server.Dto.Respone.BidResponse;
import Server.Service.BidService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    // ─────────────────────────────────────────────────────────
    // POST /api/bids/bid
    // Body: BidRequest { auctionId, amount, bidTime }
    // ─────────────────────────────────────────────────────────

    /**
     * Đặt giá cho một phiên đấu giá.
     * Yêu cầu role: BIDDER
     */
    @PostMapping("/bid")
    public BidResponse placeBid(@RequestBody BidRequest request) {
        return bidService.placeBid(request);
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/bids/detail?id={auctionId}
    // ─────────────────────────────────────────────────────────

    /**
     * Lấy thông tin người đang dẫn đầu trong một phiên đấu giá.
     */
    @GetMapping("/detail")
    public BidResponse getBidDetail(@RequestParam String id) {
        return bidService.getBidDetail(id);
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/bids/history?id={userId}
    // ─────────────────────────────────────────────────────────

    /**
     * Lấy toàn bộ lịch sử đặt giá của một người dùng (Bidder).
     * <p>
     * Trả về danh sách UserBidHistoryResponse bao gồm:
     * - tên sản phẩm, số tiền đã đặt, thời gian đặt, trạng thái
     *
     * @param id userId của Bidder
     */
    @GetMapping("/history")
    public List<BidHistoryRespone> getMyBidHistory(@RequestParam String id) {
        return bidService.getMyBidHistory(id);
    }
}
