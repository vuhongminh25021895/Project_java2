package Server.Repository;

import Server.Model.BidTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidTransactionRepository extends JpaRepository<BidTransaction, String> {

    /**
     * Lấy toàn bộ lịch sử đặt giá của một Bidder, sắp xếp mới nhất trước.
     * Fetch JOIN để tránh N+1 khi truy cập auction.item.name.
     *
     * Dùng cho chức năng "Bid History" trên trang Profile.
     */
    @Query("""
            SELECT bt FROM BidTransaction bt
            JOIN FETCH bt.auction a
            JOIN FETCH a.item i
            WHERE bt.bidder.id = :bidderId
            ORDER BY bt.bidTime DESC
            """)
    List<BidTransaction> findByBidderIdWithAuction(@Param("bidderId") String bidderId);
}
