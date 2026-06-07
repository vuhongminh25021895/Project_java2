package Server.Dto.Respone;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidRespone(
        boolean success,

        String username,

        BigDecimal highestBid,

        LocalDateTime startime,

        LocalDateTime endtime
) {

}
