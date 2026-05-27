package app.server.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(indexes = {
        @Index(name = "idx_bid_auction", columnList = "auction_id"),
        @Index(name = "idx_bid_bidder", columnList = "bidder_id")
})
public class BidTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Bidder bidder;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal bidAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Auction auction;

    public BidTransaction() {}

    public BidTransaction(Bidder bidder, Auction auction, BigDecimal bidAmount) {
        this.bidder = bidder;
        this.auction = auction;
        this.bidAmount = bidAmount;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public Bidder getBidder() {
        return bidder;
    }

    public Auction getAuction() {
        return auction;
    }
}
