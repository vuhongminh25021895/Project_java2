package app.server.repository;

import app.server.model.BidTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidTransactionRepository extends JpaRepository<BidTransaction, String> {
}
