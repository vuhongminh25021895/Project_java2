package app.server.dto.response;

import java.math.BigDecimal;

public class BidResponse {
    private boolean success;
    private String message;
    private BigDecimal highestBid;
    private String highestBidderId;

    public BidResponse() {}

    public BidResponse(boolean success,
                       String message,
                       BigDecimal highestBid,
                       String highestBidderId) {

        this.success = success;
        this.message = message;
        this.highestBid = highestBid;
        this.highestBidderId = highestBidderId;
    }

    public static BidResponse success(
            BigDecimal highestBid,
            String highestBidderId
    ) {

        return new BidResponse(
                true,
                "Bid placed successfully",
                highestBid,
                highestBidderId
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public BigDecimal getHighestBid() {
        return highestBid;
    }

    public String getHighestBidderId() {
        return highestBidderId;
    }
}
