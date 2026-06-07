package Server.Model;

import Server.Enums.AuctionStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Auction extends BaseEntity {

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

