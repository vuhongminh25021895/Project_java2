package Server.Service;

import Server.Dto.Respone.AuctionCardRespone;
import Server.Dto.Respone.BidHistoryRespone;
import Server.Dto.Respone.MyItemResponse;
import Server.Dto.Respone.ProfileRespone;
import Server.Enums.AuctionStatus;
import Server.Exception.UserNotFoundException;
import Server.Mapper.AuctionCardMapper;
import Server.Model.*;
import Server.Repository.AuctionRepository;
import Server.Repository.BidTransactionRepository;
import Server.Repository.ItemRepository;
import Server.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidTransactionRepository bidTransactionRepository;
    private final ItemRepository            itemRepository;

    public ProfileService(
            UserRepository userRepository,
            AuctionRepository auctionRepository,
            BidTransactionRepository bidTransactionRepository,
            ItemRepository itemRepository
    ) {
        this.userRepository           = userRepository;
        this.auctionRepository        = auctionRepository;
        this.bidTransactionRepository = bidTransactionRepository;
        this.itemRepository           = itemRepository;
    }

    // ─────────────────────────────────────────────────────────
    // 1. GET PROFILE
    //    GET /api/profile
    //    JWT trong header → SecurityContext → userId
    // ─────────────────────────────────────────────────────────

    /**
     * Trả về thông tin tổng hợp của người dùng hiện tại.
     *
     * Với Seller: revenue = tổng giá cao nhất của các phiên FINISHED/PAID,
     *             productscount = số sản phẩm đã đăng.
     * Với Bidder: revenue = 0, productscount = 0.
     */
    public ProfileRespone getProfile() {
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        int participatedAuctions = 0;
        int wonAuctions          = 0;
        double revenue           = 0.0;
        int productscount        = 0;

        if (user instanceof Bidder bidder) {
            // Số phiên đã tham gia = số auction mà bidder có ít nhất 1 bid
            participatedAuctions = auctionRepository
                    .findByBidderIdFetch(userId).size();

            // Số phiên đã thắng = auction FINISHED/PAID mà highest bid là của bidder
            wonAuctions = (int) auctionRepository
                    .findByBidderIdFetch(userId)
                    .stream()
                    .filter(a -> a.getHighestBid() != null
                            && a.getHighestBid().getBidder().getId().equals(userId)
                            && (a.getAuctionStatus() == AuctionStatus.FINISHED
                            ||  a.getAuctionStatus() == AuctionStatus.PAID))
                    .count();

        } else if (user instanceof Seller seller) {
            productscount = seller.getListItems().size();

            // Doanh thu = tổng highestBid của các phiên đã kết thúc
            revenue = auctionRepository
                    .findBySellerIdFetch(userId)
                    .stream()
                    .filter(a -> a.getAuctionStatus() == AuctionStatus.FINISHED
                            || a.getAuctionStatus() == AuctionStatus.PAID)
                    .filter(a -> a.getHighestBid() != null)
                    .mapToDouble(a -> a.getHighestBid()
                            .getBidAmount().doubleValue())
                    .sum();
        }

        return new ProfileRespone(
                true,
                user.getId(),
                user.getFullName(),
                user.getUserName(),
                user.getEmail(),
                user.getCreatedAt(),
                participatedAuctions,
                wonAuctions,
                revenue,
                productscount
        );
    }

    // ─────────────────────────────────────────────────────────
    // 2. GET BID HISTORY
    //    GET /api/bids/history?id={userId}
    // ─────────────────────────────────────────────────────────

    /**
     * Trả về toàn bộ lịch sử đặt giá của người dùng.
     *
     * Mỗi BidTransaction được ánh xạ thành UserBidHistoryResponse với:
     *   - biddername → tên sản phẩm (hiển thị ở cột "Sản phẩm")
     *   - amount     → số tiền đã đặt
     *   - bidTime    → thời điểm đặt
     *   - status     → WINNING nếu đây là highest bid, OUTBID nếu đã bị vượt
     *
     * @param userId ID của Bidder cần lấy lịch sử
     */
    public List<BidHistoryRespone> getMyBidHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!(user instanceof Bidder)) {
            return List.of(); // Seller không có lịch sử đấu giá
        }

        List<BidTransaction> transactions =
                bidTransactionRepository.findByBidderIdWithAuction(userId);

        List<BidHistoryRespone> result = new ArrayList<>();

        for (BidTransaction bt : transactions) {
            Auction auction = bt.getAuction();

            // Xác định status của lần đặt giá này
            String status = resolveStatus(bt, auction);

            result.add(new BidHistoryRespone(
                    true,
                    bt.getBidder().getUserName(),// cột "Sản phẩm"
                    bt.getBidAmount(),
                    bt.getBidTime(),
                    status
            ));
        }

        return result;
    }

    /**
     * Xác định trạng thái của một BidTransaction:
     *  - WON      : phiên đã kết thúc và đây là highest bid
     *  - LOST     : phiên đã kết thúc và không phải highest bid
     *  - WINNING  : phiên đang mở và đây là highest bid hiện tại
     *  - OUTBID   : phiên đang mở nhưng đã có người đặt cao hơn
     */
    private String resolveStatus(BidTransaction bt, Auction auction) {
        boolean isHighest = auction.getHighestBid() != null
                && auction.getHighestBid().getId().equals(bt.getId());

        boolean isEnded = auction.getAuctionStatus() == AuctionStatus.FINISHED
                || auction.getAuctionStatus() == AuctionStatus.PAID
                || auction.getAuctionStatus() == AuctionStatus.CANCELED;

        if (isEnded) {
            return isHighest ? "WON" : "LOST";
        } else {
            return isHighest ? "WINNING" : "OUTBID";
        }
    }

    // ─────────────────────────────────────────────────────────
    // 3. GET ALL MY PRODUCTS
    //    GET /api/products/getitems?id={userId}
    // ─────────────────────────────────────────────────────────

    /**
     * Trả về danh sách tất cả sản phẩm mà Seller đã đăng,
     * kèm giá hiện tại và trạng thái của phiên đấu giá tương ứng.
     *
     * @param userId ID của Seller
     */
    public List<MyItemResponse> getAllMyItems(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!(user instanceof Seller)) {
            return List.of(); // Bidder không có sản phẩm
        }

        List<Item> items = itemRepository.findBySellerIdWithAuction(userId);
        List<MyItemResponse> result = new ArrayList<>();

        for (Item item : items) {
            // Tìm phiên đấu giá của sản phẩm này
            Auction auction = auctionRepository
                    .findAll()
                    .stream()
                    .filter(a -> a.getItem().getId().equals(item.getId()))
                    .findFirst()
                    .orElse(null);

            BigDecimal currentPrice;
            String status;

            if (auction == null) {
                // Sản phẩm chưa được tạo phiên đấu giá
                currentPrice = item.getStartingPrice();
                status       = "PENDING";
            } else {
                currentPrice = auction.getHighestBid() != null
                        ? auction.getHighestBid().getBidAmount()
                        : item.getStartingPrice();
                status = auction.getAuctionStatus().name();
            }

            result.add(new MyItemResponse(
                    true,
                    item.getId(),
                    item.getName(),
                    currentPrice,
                    status,
                    auction != null ? auction.getEndTime() : null
            ));
        }

        return result;
    }

    // ─────────────────────────────────────────────────────────
    // 4. GET ALL MY AUCTIONS (dùng lại trong AuctionController)
    //    GET /api/auctions/getcards?id={userId}
    // ─────────────────────────────────────────────────────────

    /**
     * Trả về danh sách phiên đấu giá liên quan đến userId:
     *  - Nếu là Seller → các phiên do seller tạo
     *  - Nếu là Bidder → các phiên bidder đã tham gia đặt giá
     *
     * @param userId ID người dùng
     */
    public List<AuctionCardRespone> getAllMyAuctions(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Auction> auctions;

        if (user instanceof Seller) {
            auctions = auctionRepository.findBySellerIdFetch(userId);
        } else {
            auctions = auctionRepository.findByBidderIdFetch(userId);
        }

        return auctions.stream()
                .map(AuctionCardMapper::toCard)
                .toList();
    }

    // ─────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────

    private String getCurrentUserId() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
