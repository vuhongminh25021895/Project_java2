package Dto.Respone;

import java.time.LocalDateTime;
import java.util.List;

public record AuctionDetailRespone(
        boolean success,

        Long auctionid,

        String title,

        String description,

        Double currentPrice,

        Integer bidCount,

        String sellerName,

        LocalDateTime startTime,

        LocalDateTime endTime,

        String status,

        List<BidHistoryRespone> bidHistoryResponeList
) {}
