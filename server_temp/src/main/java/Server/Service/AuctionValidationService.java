package Server.Service;

import java.math.BigDecimal;

import Server.Enums.AuctionStatus;
import Server.Model.Auction;
import Server.Model.Bidder;
import Server.Exception.AuctionAlreadyClosedException;
import Server.Exception.BidTooLowException;
import Server.Exception.SelfBidException;
import org.springframework.stereotype.Service;

@Service
public class AuctionValidationService {
    public void validateBid(
            Auction auction,
            Bidder bidder,
            BigDecimal amount
    ) {

        if (auction.getAuctionStatus() != AuctionStatus.RUNNING) {
            throw new AuctionAlreadyClosedException(auction.getId(), auction.getAuctionStatus()
            );
        }

        if (auction.isExpired()) {
            throw new AuctionAlreadyClosedException(auction.getId(), auction.getAuctionStatus()
            );
        }

        BigDecimal currentPrice = auction.getHighestBid().getBidAmount();

        if (amount.compareTo(currentPrice) <= 0) {
            throw new BidTooLowException(currentPrice, amount);
        }

        if (auction.getItem()
                .getSeller()
                .getId()
                .equals(bidder.getId())) {

            throw new SelfBidException(
                    "Seller cannot bid on own auction"
            );
        }
    }
}
