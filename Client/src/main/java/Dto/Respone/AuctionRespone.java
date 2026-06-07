package Dto.Respone;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionRespone(
        boolean success,
        String auctionId,
        String currentWinusername,
        BigDecimal currentamount,
        LocalDateTime start,
        LocalDateTime end
) {}
