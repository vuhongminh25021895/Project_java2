package app.server.dto.request;

import java.time.LocalDateTime;

public class AuctionRequest {

    private String itemId;

    private String sellerId;

    private String userId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public AuctionRequest() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(
            String itemId
    ) {
        this.itemId = itemId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(
            String sellerId
    ) {
        this.sellerId = sellerId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(
            LocalDateTime startTime
    ) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(
            LocalDateTime endTime
    ) {
        this.endTime = endTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}