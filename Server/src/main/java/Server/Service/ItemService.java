package Server.Service;

import Server.Dto.Request.ProductPublishRequest;
import Server.Dto.Respone.ProductPublishResponse;
import Server.Enums.AuctionStatus;
import Server.Enums.ItemCategory;
import Server.Exception.UserNotFoundException;
import Server.Factory.ItemFactory;
import Server.Model.Auction;
import Server.Model.Item;
import Server.Model.Seller;
import Server.Model.User;
import Server.Repository.AuctionRepository;
import Server.Repository.ItemRepository;
import Server.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;

    public ItemService(
            UserRepository userRepository,
            ItemRepository itemRepository,
            AuctionRepository auctionRepository
    ) {
        this.userRepository    = userRepository;
        this.itemRepository    = itemRepository;
        this.auctionRepository = auctionRepository;
    }

    // ─────────────────────────────────────────────────────────
    // PUBLISH PRODUCT
    // POST /api/products/publish
    // ─────────────────────────────────────────────────────────

    /**
     * Tạo Item + Auction mới từ request của Seller.
     *
     * @param request dữ liệu từ client (ProductPublishRequest)
     * @return ProductPublishResponse
     */
    @Transactional
    @PreAuthorize("hasRole('SELLER')")
    public ProductPublishResponse publishProduct(ProductPublishRequest request) {

        // 1. Lấy Seller từ SecurityContext (không tin userId trong request)
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!(user instanceof Seller seller)) {
            return new ProductPublishResponse(false, null, null,
                    "Chỉ Seller mới có thể đăng sản phẩm.");
        }

        // 2. Validate thời gian kết thúc
        if (request.endTime() == null || request.endTime().isBefore(LocalDateTime.now())) {
            return new ProductPublishResponse(false, null, null,
                    "Thời gian kết thúc không hợp lệ.");
        }

        // 3. Map category
        ItemCategory category = parseCategory(request.category());
        if (category == null) {
            return new ProductPublishResponse(false, null, null,
                    "Danh mục sản phẩm không hợp lệ: " + request.category());
        }

        // 4. Tạo Item thông qua Factory (Art / Electronics / Vehicle)
        Item item = ItemFactory.createItem(category);
        item.setName(request.productName());
        item.setDescription(request.description());
        item.setStartingPrice(request.initPrice());
        item.setSeller(seller);

        // Nếu model Item có trường iaPrice (Instant Accept Price), gán tại đây.
        // Nếu chưa có, cân nhắc thêm field vào Item hoặc Auction.
        // item.setInstantAcceptPrice(request.iaPrice());

        Item savedItem = itemRepository.save(item);
        seller.addItem(savedItem); // Đồng bộ quan hệ 2 chiều

        // 5. Tạo Auction với trạng thái OPEN
        Auction auction = Auction.builder()
                .item(savedItem)
                .auctionStatus(AuctionStatus.OPEN)
                .startTime(LocalDateTime.now())
                .endTime(request.endTime())
                .build();

        Auction savedAuction = auctionRepository.save(auction);

        return new ProductPublishResponse(
                true,
                savedItem.getId(),
                savedAuction.getId(),
                "Đăng sản phẩm thành công."
        );
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────

    /**
     * Chuyển đổi chuỗi category từ FXML ChoiceBox sang ItemCategory enum.
     * FXML dùng: "Art" | "Electronics" | "Vehicles"
     */
    private ItemCategory parseCategory(String raw) {
        if (raw == null) return null;
        return switch (raw.trim().toUpperCase()) {
            case "ART"         -> ItemCategory.ART;
            case "ELECTRONICS" -> ItemCategory.ELECTRONICS;
            case "VEHICLES", "VEHICLE" -> ItemCategory.VEHICLE;
            default            -> null;
        };
    }

    private String getCurrentUserId() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
