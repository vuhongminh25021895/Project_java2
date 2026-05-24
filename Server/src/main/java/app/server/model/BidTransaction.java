package app.server.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class BidTransaction extends BaseEntity {
    @ManyToOne
    private Bidder bidder;
    private double amount;
    private LocalDateTime time;
    @ManyToOne
    private Auction auction;

    public BidTransaction() {}

    public BidTransaction(Bidder bidder, Auction auction, double amount) {
        this.bidder = bidder;
        this.auction = auction;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (time == null) {
            time = LocalDateTime.now();
        }
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Bidder getBidder() {
        return bidder;
    }

    public Auction getAuction() {
        return auction;
    }
}
