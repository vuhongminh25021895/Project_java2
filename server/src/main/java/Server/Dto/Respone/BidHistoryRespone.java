package Server.Dto.Respone;

import java.math.BigDecimal;

public record BidHistoryRespone(
        String biddername,

        BigDecimal amount,

        String bidTime

){}
