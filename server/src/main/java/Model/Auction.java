package Model;

import java.time.LocalDateTime;
import java.util.List;

public class Auction {
    private String id;
    private Item item;
    private Seller seller;
    private AuctionStatus auctionStatus;
    private List<BidTransaction> bids;
    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private double highestBid = 0;
    private String highestBidderId;

    public Auction(String id, Item item, Seller seller, LocalDateTime endTime) {
        this.id = id;
        this.item = item;
        this.seller = seller;
        this.endTime = endTime;
        this.auctionStatus = AuctionStatus.OPEN;
    }

    public synchronized boolean placeBid(User bidder, double amount) {

        if (auctionStatus == AuctionStatus.FINISHED
                || auctionStatus == AuctionStatus.CANCELED
                || LocalDateTime.now().isAfter(endTime)) {
            return false;
        }

        if (amount <= highestBid) {
            return false;
        }

        if (((Bidder) bidder).getBalance() < amount) {
            return false;
        }

        highestBid = amount;
        highestBidderId = bidder.getId();

        bids.add(new BidTransaction(bidder.getId(), amount));

        return true;
    }
}
