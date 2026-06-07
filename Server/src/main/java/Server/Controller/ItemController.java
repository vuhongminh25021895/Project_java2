package Server.Controller;

import Server.Dto.Request.ProductPublishRequest;
import Server.Dto.Respone.MyItemResponse;
import Server.Dto.Respone.ProductPublishResponse;
import Server.Model.Item;
import Server.Service.ItemService;
import Server.Service.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ItemController {

    private final ItemService itemService;
    private final ProfileService profileService;

    public ItemController(ItemService itemService, ProfileService profileService) {
        this.itemService = itemService;
        this.profileService = profileService;
    }

    // ─────────────────────────────────────────────────────────
    // POST /api/products/publish
    // Body: ProductPublishRequest
    // ─────────────────────────────────────────────────────────

    /**
     * Tạo Item + Auction mới từ thông tin do Seller cung cấp.
     *
     * Yêu cầu role: SELLER (kiểm tra bằng @PreAuthorize trong Service).
     * userId thực sự lấy từ JWT (SecurityContext), không tin request body.
     *
     * @param request dữ liệu sản phẩm từ form ProductPublishing
     * @return ProductPublishResponse { success, productId, auctionId, message }
     */
    @PostMapping("/publish")
    public ProductPublishResponse publishProduct(@RequestBody ProductPublishRequest request) {
        return itemService.publishProduct(request);
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/products/getitems?id={userId}
    // ─────────────────────────────────────────────────────────

    /**
     * Lấy toàn bộ sản phẩm của một Seller, kèm giá hiện tại
     * và trạng thái phiên đấu giá tương ứng.
     *
     * @param id userId của Seller
     * @return danh sách MyItemResponse
     */
    @GetMapping("/getitems")
    public List<MyItemResponse> getMyItems(@RequestParam String id) {
        return profileService.getAllMyItems(id);
    }
}
