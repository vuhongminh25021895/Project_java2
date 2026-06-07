package Dto.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AuctionDetailResponse(

    boolean success,

    String auctionid,

    String title,

    String description,

    BigDecimal currentPrice,

    Integer bidCount,

    String sellerName,

    LocalDateTime startTime,

    LocalDateTime endTime,

    String status,

    List<BidHistoryResponse> bidHistoryResponseList) {
}
