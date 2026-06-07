package Server.Dto.Respone;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MyItemResponse(
        boolean success,
        String productId,
        String productName,
        BigDecimal currentPrice,
        String status,
        LocalDateTime endTime
) {
}
