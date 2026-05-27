package app.server.dto.response;

import app.server.enums.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionResponse {

    private String auctionId;

    private String itemName;

    private BigDecimal currentPrice;

    private String highestBidderId;

    private AuctionStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int viewerCount;

    public AuctionResponse() {
    }

    public AuctionResponse(
            String auctionId,
            String itemName,
            BigDecimal currentPrice,
            String highestBidderId,
            AuctionStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int viewerCount
    ) {

        this.auctionId = auctionId;

        this.itemName = itemName;

        this.currentPrice = currentPrice;

        this.highestBidderId =
                highestBidderId;

        this.status = status;

        this.startTime = startTime;

        this.endTime = endTime;

        this.viewerCount = viewerCount;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public String getHighestBidderId() {
        return highestBidderId;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getViewerCount() {
        return viewerCount;
    }
}