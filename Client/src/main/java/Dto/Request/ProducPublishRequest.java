package Dto.Request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProducPublishRequest(
        String userId,          // ID người đăng (lấy từ ClientSession)
        String productName,     // Tên sản phẩm
        String category,        // Danh mục: Art / Electronics / Vehicles
        String description,     // Mô tả chi tiết
        BigDecimal initPrice,   // Giá khởi điểm
        BigDecimal iaPrice,     // Giá chốt ngay lập tức (Instant Accept)
        LocalDateTime endTime   // Thời điểm kết thúc đấu giá
) {
}
