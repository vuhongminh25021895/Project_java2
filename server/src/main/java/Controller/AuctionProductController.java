package Controller;

import Dao.AuctionProductDao;
import Dto.AuctionProductItem;
import Dto.BidHistoryPoint;
import Dto.BidUpdateEvent;
import Dto.AuctionProductRequest;
import Dto.AuctionProductResponse;
import Dto.BidRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class AuctionProductController {
    private final AuctionProductDao auctionProductDao;

    public AuctionProductController() {
        auctionProductDao = new AuctionProductDao();
    }

    @PostMapping
    public AuctionProductResponse publishProduct(@RequestBody AuctionProductRequest request) {
        try {
            if (request == null
                    || isBlank(request.getSellerUsername())
                    || isBlank(request.getCategory())
                    || isBlank(request.getProductName())
                    || request.getStartPrice() == null
                    || request.getBuyNowPrice() == null
                    || isBlank(request.getEndTime())) {
                return new AuctionProductResponse(false, "Thông tin sản phẩm không hợp lệ.");
            }

            if (request.getStartPrice() <= 0 || request.getBuyNowPrice() < request.getStartPrice()) {
                return new AuctionProductResponse(false, "Giá sản phẩm không hợp lệ.");
            }

            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
            if (endTime.isBefore(LocalDateTime.now())) {
                return new AuctionProductResponse(false, "Thời gian kết thúc phải ở tương lai.");
            }

            boolean success = auctionProductDao.create(request);
            if (success) {
                return new AuctionProductResponse(true, "Đăng sản phẩm thành công.");
            }

            return new AuctionProductResponse(false, "Không thể lưu sản phẩm vào cơ sở dữ liệu.");
        } catch (DateTimeParseException e) {
            return new AuctionProductResponse(false, "Thời gian kết thúc không đúng định dạng.");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuctionProductResponse(false, "Server error");
        }
    }

    @GetMapping
    public List<AuctionProductItem> getProducts() {
        try {
            return auctionProductDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamBidUpdates() {
        return BidEventHub.getInstance().subscribe();
    }

    @GetMapping("/{productId}/history")
    public List<BidHistoryPoint> getBidHistory(@PathVariable int productId) {
        try {
            if (productId <= 0) {
                return Collections.emptyList();
            }

            return auctionProductDao.findBidHistory(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @PostMapping("/{productId}/bid")
    public AuctionProductResponse placeBid(@PathVariable int productId, @RequestBody BidRequest request) {
        try {
            if (productId <= 0 || request == null || request.getBidAmount() == null || request.getBidAmount() <= 0) {
                return new AuctionProductResponse(false, "Giá đặt không hợp lệ.");
            }

            if (request.getBidderUsername() == null || request.getBidderUsername().isBlank()) {
                return new AuctionProductResponse(false, "Vui lòng đăng nhập trước khi đặt giá.");
            }

            AuctionProductResponse response = auctionProductDao.placeBid(productId, request.getBidAmount(), request.getBidderUsername());
            if (response.isSuccess() && response.getCurrentPrice() != null) {
                BidUpdateEvent event = new BidUpdateEvent();
                event.setProductId(productId);
                event.setCurrentPrice(response.getCurrentPrice());
                event.setStatus(response.getStatus());
                event.setBidTime(response.getBidTime());
                event.setEndTime(response.getEndTime());
                event.setPreviousHighestBidderUsername(response.getPreviousHighestBidderUsername());
                event.setHighestBidderUsername(response.getHighestBidderUsername());
                event.setPreviousHighestBidderBalance(response.getPreviousHighestBidderBalance());
                event.setHighestBidderBalance(response.getHighestBidderBalance());
                BidEventHub.getInstance().publish(event);
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return new AuctionProductResponse(false, "Server error");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
