package Dto;

public class BidRequest {
    private String bidderUsername;
    private Integer bidAmount;

    public BidRequest() {
    }

    public BidRequest(String bidderUsername, Integer bidAmount) {
        this.bidderUsername = bidderUsername;
        this.bidAmount = bidAmount;
    }

    public String getBidderUsername() {
        return bidderUsername;
    }

    public void setBidderUsername(String bidderUsername) {
        this.bidderUsername = bidderUsername;
    }

    public Integer getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Integer bidAmount) {
        this.bidAmount = bidAmount;
    }
}
