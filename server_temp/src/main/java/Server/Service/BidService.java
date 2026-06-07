package Server.Service;

import Server.Dto.Request.BidRequest;
import Server.Dto.Respone.BidRespone;
import Server.Exception.AuctionAlreadyClosedException;
import Server.Model.Auction;
import Server.Model.BidTransaction;
import Server.Model.Bidder;
import Server.Model.User;
import Server.Repository.AuctionRepository;
import Server.Repository.BidTransactionRepository;
import Server.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import Server.Exception.UserNotFoundException;
import Server.Exception.AuctionNotfoundException;

import java.math.BigDecimal;

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
    @PreAuthorize("hasRole('BIDDER')")
    public BidRespone placeBid(BidRequest request) {
        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Auction auction = auctionRepository
                .findByIdForUpdate(request.auctionId())
                .orElseThrow(() ->
                        new AuctionNotfoundException(request.auctionId())
                        );
        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(userId));
        Bidder bidder = (Bidder) user;

        if (!auction.hasViewer(user)) {
            throw new UserNotFoundException(userId);
        }
        if (!auction.isRunning()) {
            throw new AuctionAlreadyClosedException(request.auctionId(), auction.getAuctionStatus());
        }

        validationService.validateBid(auction, bidder, request.amount());
        BidTransaction bidTransaction = BidTransaction.builder()
                        .bidder(bidder)
                                .bidAmount(request.amount())
                                        .bidTime(request.bidTime())
                                                .auction(auction)
                                                        .build();
        auctionEngine.processBid(auction, bidTransaction);
        auctionRepository.save(auction);
        BigDecimal highestAmount = bidTransaction.getBidAmount();
        BidRespone respone = new BidRespone(true, user.getUserName(), highestAmount  , null, null );
        return respone;
    }
}
