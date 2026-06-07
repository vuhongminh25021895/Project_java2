package Server.Repository;

import Server.Model.BidTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidTransactionRepository extends JpaRepository<BidTransaction, String> {
}
