package app.client.dto.response;

import app.client.enums.AuctionStatus;

import java.time.LocalDateTime;

public class AuctionResponse {

    private String auctionId;

    private String itemName;

    private double currentPrice;

    private String highestBidderId;

    private AuctionStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int participantCount;

    public AuctionResponse() {
    }

    public AuctionResponse(
            String auctionId,
            String itemName,
            double currentPrice,
            String highestBidderId,
            AuctionStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int participantCount
    ) {

        this.auctionId = auctionId;

        this.itemName = itemName;

        this.currentPrice = currentPrice;

        this.highestBidderId =
                highestBidderId;

        this.status = status;

        this.startTime = startTime;

        this.endTime = endTime;

        this.participantCount = participantCount;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public String getItemName() {
        return itemName;
    }

    public double getCurrentPrice() {
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

    public int getParticipantCount() {
        return participantCount;
    }
}