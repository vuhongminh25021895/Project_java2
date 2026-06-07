package Server.Mapper;

import Server.Dto.Respone.AuctionCardRespone;
import Server.Model.Auction;

import java.math.BigDecimal;

public class AuctionCardMapper {
    public static AuctionCardRespone toCard(Auction auction) {
        BigDecimal currentPrice = BigDecimal.ZERO;
        if (auction.getHighestBid() != null) {
            currentPrice = auction.getHighestBid().getBidAmount();
        }
        return new AuctionCardRespone(
                false,
                auction.getId(),
                auction.getItem().getId(),
                auction.getItem().getName(),
                currentPrice,
                auction.getAuctionStatus().name(),
                auction.getEndTime(),
                auction.getItem().getSeller().getUserName()
        );
    }
}
