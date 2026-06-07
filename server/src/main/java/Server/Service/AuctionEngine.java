package Server.Service;

import Server.Model.Auction;
import Server.Model.BidTransaction;
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
