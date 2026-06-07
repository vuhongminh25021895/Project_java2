package Server.Dto.Request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductPublishRequest(
        String userId,          // ID người đăng (Seller)
        String productName,     // Tên sản phẩm
        String category,        // "Art" | "Electronics" | "Vehicles"
        String description,     // Mô tả chi tiết
        BigDecimal initPrice,   // Giá khởi điểm
        BigDecimal iaPrice,     // Giá chốt ngay lập tức
        LocalDateTime endTime   // Thời điểm kết thúc đấu giá
) {
}
