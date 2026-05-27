package app.server.model;

import app.server.enums.AuctionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Auction extends BaseEntity {

    @Version
    private Long version;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus auctionStatus;

    @OneToMany(
            mappedBy = "auction",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BidTransaction> bidHistory = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "highest_bid_id")
    private BidTransaction highestBid;

    @Transient
    private Set<User> viewers = new HashSet<>();

    public Auction() {}

    public Auction(Item item, LocalDateTime startTime, LocalDateTime endTime) {
        this.item = item;
        this.startTime = startTime;
        this.endTime = endTime;

        this.auctionStatus = AuctionStatus.OPEN;
    }

    public void recordBid(BidTransaction bid) {

        this.highestBid = bid;

        this.bidHistory.add(bid);
    }

    public void extendAuctionSeconds(long seconds) {

        this.endTime = this.endTime.plusSeconds(seconds);
    }

    public boolean isExpired() {

        return LocalDateTime.now().isAfter(this.endTime);
    }

    public boolean isRunning() {

        return auctionStatus == AuctionStatus.RUNNING;
    }

    public BidTransaction getHighestBid() {
        return highestBid;
    }

    public BigDecimal getCurrentPrice() {

        if (highestBid == null) {
            return item.getStartingPrice();
        }

        return highestBid.getBidAmount();
    }

    public Bidder getHighestBidder() {

        if (highestBid == null) {
            return null;
        }

        return highestBid.getBidder();
    }

    public Item getItem() {
        return item;
    }

    public AuctionStatus getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(
            AuctionStatus auctionStatus
    ) {
        this.auctionStatus = auctionStatus;
    }

    public List<BidTransaction> getBidHistory() {
        return bidHistory;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getHighestBidderId() {

        if (highestBid == null) {
            return null;
        }

        return highestBid.getBidder().getId();
    }

    public void joinAuction(User user) {

        viewers.add(user);
    }

    public void leaveAuction(User user) {

        viewers.remove(user);
    }

    public boolean hasViewer(User user) {

        return viewers.contains(user);
    }

    public int getViewerCount() {

        return viewers.size();
    }
}
