package Dto;

public class AuctionProductResponse {
    private boolean success;
    private String message;
    private Integer currentPrice;
    private String status;
    private String bidTime;
    private String endTime;
    private String highestBidderUsername;
    private String previousHighestBidderUsername;
    private Long highestBidderBalance;
    private Long previousHighestBidderBalance;
    private Long newBalance;

    public AuctionProductResponse() {
    }

    public AuctionProductResponse(boolean success, String message) {
        this(success, message, null, null, null);
    }

    public AuctionProductResponse(boolean success, String message, Integer currentPrice, String status, String bidTime) {
        this.success = success;
        this.message = message;
        this.currentPrice = currentPrice;
        this.status = status;
        this.bidTime = bidTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBidTime() {
        return bidTime;
    }

    public void setBidTime(String bidTime) {
        this.bidTime = bidTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getHighestBidderUsername() {
        return highestBidderUsername;
    }

    public void setHighestBidderUsername(String highestBidderUsername) {
        this.highestBidderUsername = highestBidderUsername;
    }

    public String getPreviousHighestBidderUsername() {
        return previousHighestBidderUsername;
    }

    public void setPreviousHighestBidderUsername(String previousHighestBidderUsername) {
        this.previousHighestBidderUsername = previousHighestBidderUsername;
    }

    public Long getHighestBidderBalance() {
        return highestBidderBalance;
    }

    public void setHighestBidderBalance(Long highestBidderBalance) {
        this.highestBidderBalance = highestBidderBalance;
    }

    public Long getPreviousHighestBidderBalance() {
        return previousHighestBidderBalance;
    }

    public void setPreviousHighestBidderBalance(Long previousHighestBidderBalance) {
        this.previousHighestBidderBalance = previousHighestBidderBalance;
    }

    public Long getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Long newBalance) {
        this.newBalance = newBalance;
    }
}
