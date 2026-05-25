package Dto;

public class BidUpdateEvent {
    private Integer productId;
    private Integer currentPrice;
    private String status;
    private String bidTime;

    public BidUpdateEvent() {
    }

    public BidUpdateEvent(Integer productId, Integer currentPrice, String status, String bidTime) {
        this.productId = productId;
        this.currentPrice = currentPrice;
        this.status = status;
        this.bidTime = bidTime;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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
