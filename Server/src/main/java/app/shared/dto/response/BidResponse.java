package app.shared.dto.response;

public class BidResponse {
    private boolean success;
    private String message;
    private double highestBid;
    private String highestBidderId;

    public BidResponse(boolean success,
                       String message,
                       double highestBid,
                       String highestBidderId) {

        this.success = success;
        this.message = message;
        this.highestBid = highestBid;
        this.highestBidderId = highestBidderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public String getHighestBidderId() {
        return highestBidderId;
    }
}
