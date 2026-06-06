package Dto.Respone;

import java.time.LocalDateTime;

public record AuctionCardRespone(
        boolean success,
        Long auctionId,
        Long productId,
        String productName,
        String imageUrl,
        Double currentPrice,
        Double startPrice,
        String status,
        LocalDateTime endTime,
        String sellerName
) {}
