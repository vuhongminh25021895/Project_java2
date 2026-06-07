package Server.Service;

import Server.Enums.AuctionStatus;
import Server.Model.Auction;
import Server.Repository.AuctionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionSchedulerService {
    private final AuctionRepository auctionRepository;

    public AuctionSchedulerService(
            AuctionRepository auctionRepository
    ) {
        this.auctionRepository = auctionRepository;
    }

    @Scheduled(fixedRate = 1000)
    public void updateAuctionStatus() {

        LocalDateTime now =
                LocalDateTime.now();

        List<Auction> openAuctions =
                auctionRepository
                        .findByAuctionStatus(
                                AuctionStatus.OPEN
                        );

        for (Auction auction : openAuctions) {

            if (
                    now.isAfter(
                            auction.getStartTime()
                    )
            ) {

                auction.setAuctionStatus(
                        AuctionStatus.RUNNING
                );

                auctionRepository.save(auction);
            }
        }

        List<Auction> runningAuctions =
                auctionRepository
                        .findByAuctionStatus(
                                AuctionStatus.RUNNING
                        );

        for (Auction auction : runningAuctions) {

            if (
                    now.isAfter(
                            auction.getEndTime()
                    )
            ) {

                auction.setAuctionStatus(
                        AuctionStatus.FINISHED
                );

                auctionRepository.save(auction);
            }
        }
    }
}
