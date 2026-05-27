package app.server.mapper;

import app.server.dto.response.AuctionResponse;
import app.server.model.Auction;

public class AuctionMapper {

    public static AuctionResponse toResponse(
            Auction auction
    ) {

        return new AuctionResponse(
                auction.getId(),
                auction.getItem().getName(),
                auction.getCurrentPrice(),
                auction.getHighestBidderId(),
                auction.getAuctionStatus(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getViewerCount()
        );
    }
}