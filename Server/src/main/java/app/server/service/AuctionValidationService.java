package app.server.service;

import app.server.enums.AuctionStatus;
import app.server.exception.AuctionClosedException;
import app.server.exception.InvalidBidException;
import app.server.model.Auction;
import app.server.model.Bidder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AuctionValidationService {
    public void validateBid(
            Auction auction,
            Bidder bidder,
            BigDecimal amount
    ) {

        if (auction.getAuctionStatus() != AuctionStatus.RUNNING) {
            throw new AuctionClosedException(
                    "Auction is not running"
            );
        }

        if (auction.isExpired()) {
            throw new AuctionClosedException(
                    "Auction has expired"
            );
        }

        BigDecimal currentPrice = auction.getCurrentPrice();

        if (amount.compareTo(currentPrice) <= 0) {
            throw new InvalidBidException(
                    "Bid amount must be higher than current highest bid"
            );
        }

        if (auction.getItem()
                .getSeller()
                .getId()
                .equals(bidder.getId())) {

            throw new InvalidBidException(
                    "Seller cannot bid on own auction"
            );
        }
    }
}
