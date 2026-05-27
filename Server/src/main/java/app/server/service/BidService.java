package app.server.service;

import app.server.enums.UserRole;
import app.server.exception.AuctionClosedException;
import app.server.model.BidTransaction;
import app.server.model.Bidder;
import app.server.repository.BidTransactionRepository;
import app.server.dto.request.BidRequest;
import app.server.dto.response.BidResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private final SimpMessagingTemplate messagingTemplate;

    private final AuctionValidationService validationService;

    private final AuctionEngine auctionEngine;

    public BidService(
            AuctionRepository auctionRepository,
            UserRepository userRepository,
            BidTransactionRepository bidTransactionRepository,
            SimpMessagingTemplate messagingTemplate,
            AuctionValidationService validationService,
            AuctionEngine auctionEngine
    ) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidTransactionRepository = bidTransactionRepository;
        this.messagingTemplate = messagingTemplate;
        this.validationService = validationService;
        this.auctionEngine = auctionEngine;
    }

    @Transactional
    public BidResponse placeBid(BidRequest request) {

        Auction auction = auctionRepository
                .findByIdForUpdate(request.getAuctionId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Auction not found")
                );

        User user = userRepository
                .findById(request.getBidderId())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        if (user.getRole() != UserRole.BIDDER) {
            throw new IllegalArgumentException(
                    "User is not bidder"
            );
        }

        Bidder bidder = (Bidder) user;

        if (!auction.hasViewer(user)) {

            throw new IllegalArgumentException(
                    "User has not joined auction"
            );
        }

        if (!auction.isRunning()) {

            throw new AuctionClosedException(
                    "Auction is not running"
            );
        }

        validationService.validateBid(
                auction,
                bidder,
                request.getAmount()
        );

        BidTransaction bid = new BidTransaction(
                bidder,
                auction,
                request.getAmount()
        );

        auctionEngine.processBid(auction, bid);

        bidTransactionRepository.save(bid);

        auctionRepository.save(auction);

        BidResponse response = BidResponse.success(
                auction.getCurrentPrice(),
                auction.getHighestBidderId()
        );

        messagingTemplate.convertAndSend(
                "/topic/auction/" + auction.getId(),
                response
        );

        return response;
    }
}