package Dto.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidResponse(
        boolean success,

        String username,

        BigDecimal highestBid,

        Integer bidCount,

        LocalDateTime endtime
) {
}
