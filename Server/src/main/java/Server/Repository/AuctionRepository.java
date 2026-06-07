package Server.Repository;

import Server.Enums.AuctionStatus;
import Server.Model.Auction;
import Server.Model.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, String> {

    /** Pessimistic lock – dùng khi đặt giá để tránh race condition */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    Optional<Auction> findByIdForUpdate(@Param("id") String id);

    List<Auction> findByAuctionStatus(AuctionStatus status);

    boolean existsByItem(Item item);

    /**
     * Lấy tất cả phiên đấu giá mà một Seller đã tạo (qua Item.seller).
     * Fetch JOIN để tránh lazy-loading trong vòng lặp.
     *
     * Dùng cho "All My Auctions" trên trang Profile (Seller).
     */
    @Query("""
            SELECT a FROM Auction a
            JOIN FETCH a.item i
            WHERE i.seller.id = :sellerId
            ORDER BY a.startTime DESC
            """)
    List<Auction> findBySellerIdFetch(@Param("sellerId") String sellerId);

    /**
     * Lấy tất cả phiên đấu giá mà một Bidder đã tham gia (có ít nhất 1 BidTransaction).
     * Dùng cho "All My Auctions" trên trang Profile (Bidder).
     */
    @Query("""
            SELECT DISTINCT a FROM Auction a
            JOIN a.bidHistory bt
            WHERE bt.bidder.id = :bidderId
            ORDER BY a.startTime DESC
            """)
    List<Auction> findByBidderIdFetch(@Param("bidderId") String bidderId);
}