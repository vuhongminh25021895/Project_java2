package Server.Dto.Respone;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidHistoryRespone(
        boolean success,

        String biddername,

        BigDecimal amount,

        LocalDateTime bidTime,

        String status

){}
