package Server.Repository;

import Server.Model.Item;
import Server.Model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    /**
     * Lấy toàn bộ sản phẩm thuộc về một Seller cụ thể.
     * Dùng cho chức năng "All My Products".
     */
    List<Item> findBySeller(Seller seller);

    /**
     * Lấy sản phẩm của seller kèm thông tin phiên đấu giá (LEFT JOIN).
     * Tránh N+1 query khi cần lấy currentPrice và status từ Auction.
     */
    @Query("""
            SELECT i FROM Item i
            LEFT JOIN FETCH Auction a ON a.item = i
            WHERE i.seller.id = :sellerId
            """)
    List<Item> findBySellerIdWithAuction(@Param("sellerId") String sellerId);
}
