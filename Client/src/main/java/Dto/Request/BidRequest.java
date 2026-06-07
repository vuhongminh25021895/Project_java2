package Dto.Request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidRequest (
        String auctionId,
        BigDecimal amount,
        LocalDateTime bidtime
) {}
