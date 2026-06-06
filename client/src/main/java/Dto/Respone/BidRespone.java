package Dto.Respone;

import java.time.LocalDateTime;

public record BidRespone(
        String currentprice,

        LocalDateTime startime,

        LocalDateTime endtime
) {}
