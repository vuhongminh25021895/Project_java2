package Server.Exception;

import java.math.BigDecimal;

public class BidTooLowException extends AuctionException{
    private final BigDecimal currentHighest;
    private final BigDecimal attemptedBid;

    public BidTooLowException(BigDecimal currentHighest, BigDecimal attemptedBid) {
        super("BID_TOO_LOW",
                String.format("Giá đặt %.0f phải cao hơn giá hiện tại %.0f.", attemptedBid, currentHighest));
        this.currentHighest = currentHighest;
        this.attemptedBid   = attemptedBid;
    }

    public BigDecimal getCurrentHighest() { return currentHighest; }
    public BigDecimal getAttemptedBid()   { return attemptedBid;   }
}

