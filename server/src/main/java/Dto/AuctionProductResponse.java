package Dto;

public class AuctionProductResponse {
    private boolean success;
    private String message;
    private Integer currentPrice;
    private String status;
    private String bidTime;

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
}
