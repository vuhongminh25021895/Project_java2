package Controller;

import Dao.AuctionProductDao;
import Dto.AuctionProductItem;
import Dto.AuctionProductRequest;
import Dto.AuctionProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
