package Dto.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionCardResponse(
        boolean success,
        String auctionId,
        String productId,
        String productName,
        BigDecimal currentPrice,
        String status,
        LocalDateTime endTime,
        String sellerName
) {
}
