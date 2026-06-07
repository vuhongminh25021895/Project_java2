package Dto.Respone;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionCardRespone(
        boolean success,
        String auctionId,
        String productId,
        String productName,
        BigDecimal currentPrice,
        String status,
        LocalDateTime endTime,
        String sellerName
) {}
