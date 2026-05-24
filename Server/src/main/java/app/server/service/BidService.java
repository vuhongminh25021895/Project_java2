package app.server.service;

import app.server.model.BidTransaction;
import app.server.model.Bidder;
import app.server.repository.BidTransactionRepository;
import app.shared.dto.request.BidRequest;
import app.shared.dto.response.BidResponse;
import org.springframework.stereotype.Service;
import app.server.model.Auction;
import app.server.model.User;
import app.server.repository.AuctionRepository;
import app.server.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BidService {
    private final AuctionRepository auctionRepository;

    private final UserRepository userRepository;

    private final BidTransactionRepository bidTransactionRepository;

    public BidService(
            AuctionRepository auctionRepository,
            UserRepository userRepository,
            BidTransactionRepository bidTransactionRepository
    ) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidTransactionRepository = bidTransactionRepository;
    }

    @Transactional
    public BidResponse placeBid(BidRequest request) {
        Auction auction =
                auctionRepository
                        .findByIdForUpdate(request.getAuctionId())
                        .orElse(null);

        if (auction == null) {

            return new BidResponse(
                    false,
                    "Auction not found",
                    0,
                    null
            );
        }

        User user =
                userRepository
                        .findById(request.getBidderId())
                        .orElse(null);

        if (!(user instanceof Bidder bidder)) {

            return new BidResponse(
                    false,
                    "User is not a bidder",
                    0,
                    null
            );
        }

        if (!auction.isRunning()) {

            return new BidResponse(
                    false,
                    "Auction is not running",
                    auction.getHighestBid(),
                    auction.getHighestBidderId()
            );
        }

        if (request.getAmount() <= auction.getHighestBid()) {

            return new BidResponse(
                    false,
                    "Bid amount too low",
                    auction.getHighestBid(),
                    auction.getHighestBidderId()
            );
        }

        BidTransaction bid = new BidTransaction(
                bidder,
                auction,
                request.getAmount()
        );

        auction.placeBid(bid);

        bidTransactionRepository.save(bid);

        auctionRepository.save(auction);

        return new BidResponse(
                true,
                "Bid placed successfully",
                auction.getHighestBid(),
                auction.getHighestBidderId()
        );
    }
}
