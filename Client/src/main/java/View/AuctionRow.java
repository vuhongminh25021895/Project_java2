package View;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionRow(
        String productName,
        BigDecimal currentPrice,
        String timeremain,
        String status

) {
}
