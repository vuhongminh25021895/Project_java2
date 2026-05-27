package app.server.service;

import app.server.model.Auction;
import app.server.model.BidTransaction;
import org.springframework.stereotype.Component;

@Component
public class AuctionEngine {
    private final AntiSnipingService antiSnipingService;

    public AuctionEngine(AntiSnipingService antiSnipingService) {
        this.antiSnipingService = antiSnipingService;
    }

    public void processBid(
            Auction auction,
            BidTransaction bid
    ) {

        auction.recordBid(bid);

        antiSnipingService.extendIfNeeded(auction);
    }
}