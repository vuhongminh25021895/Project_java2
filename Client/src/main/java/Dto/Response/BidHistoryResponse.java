package Dto.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidHistoryResponse(
        boolean success,

        String biddername,

        BigDecimal amount,

        LocalDateTime bidTime,

        String status
) {
}
