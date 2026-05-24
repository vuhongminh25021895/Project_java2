package app.server.model;

import app.shared.enums.AuctionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Auction extends BaseEntity {
    @OneToOne
    private Item item;
    @ManyToOne
    private Seller seller;
    @Enumerated(EnumType.STRING)
    private AuctionStatus auctionStatus;
    @OneToMany(mappedBy = "auction")
    private List<BidTransaction> bids = new ArrayList<>();
    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private double highestBid;
    @ManyToOne
    private Bidder highestBidder;

    public Auction() {}

    public Auction(Item item, Seller seller, LocalDateTime startTime, LocalDateTime endTime) {
        this.item = item;
        this.seller = seller;
        this.startTime = startTime;
        this.endTime = endTime;

        this.auctionStatus = AuctionStatus.OPEN;

        this.highestBid = item.getStartingPrice();
    }

    public synchronized void placeBid(BidTransaction bid) {

        this.highestBid = bid.getAmount();

        this.highestBidder = bid.getBidder();

        this.bids.add(bid);
    }

    public boolean isRunning() {
        return auctionStatus == AuctionStatus.RUNNING;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public Bidder getHighestBidder() {
        return highestBidder;
    }

    public Item getItem() {
        return item;
    }

    public AuctionStatus getAuctionStatus() {
        return auctionStatus;
    }

    public List<BidTransaction> getBids() {
        return bids;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getHighestBidderId() {
        return highestBidder != null ? highestBidder.getId() : null;
    }
}
