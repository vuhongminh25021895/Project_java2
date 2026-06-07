package Server.Service;

import Server.Dto.Request.BidRequest;
import Server.Dto.Respone.BidHistoryRespone;
import Server.Dto.Respone.BidResponse;
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
import java.util.ArrayList;
import java.util.List;

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
    public BidResponse placeBid(BidRequest request) {
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
        BidResponse response =
                new BidResponse(
                        true,
                        user.getUserName(),
                        auction.getHighestBid().getBidAmount(),
                        auction.getBidHistory().size(),
                        bidTransaction.getBidTime()
                );

        messagingTemplate.convertAndSend(
                "/topic/auction/" + auction.getId(),
                response
        );

        return response;
    }

    public List<BidHistoryRespone> getMyBidHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!(user instanceof Bidder)) {
            return List.of();
        }

        List<BidTransaction> transactions =
                bidTransactionRepository.findByBidderIdWithAuction(userId);

        List<BidHistoryRespone> result = new ArrayList<>();

        for (BidTransaction bt : transactions) {
            Auction auction = bt.getAuction();
            String status   = resolveStatus(bt, auction);

            result.add(new BidHistoryRespone(
                    true,
                    auction.getItem().getName(),
                    bt.getBidAmount(),
                    bt.getBidTime(),
                    status
            ));
        }

        return result;
    }

    // ─────────────────────────────────────────────────────────
    // CHI TIẾT ĐẶT GIÁ  –  GET /api/bids/detail?id={auctionId}
    // ─────────────────────────────────────────────────────────

    public BidResponse getBidDetail(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotfoundException(auctionId));

        if (auction.getHighestBid() == null) {
            return new BidResponse(false, null, null, 0, null);
        }

        BidTransaction highest = auction.getHighestBid();
        return new BidResponse(
                true,
                highest.getBidder().getUserName(),
                highest.getBidAmount(),
                auction.getBidHistory().size(),
                highest.getBidTime()
        );
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────

    /**
     * Xác định trạng thái của một lần đặt giá:
     *  - WON / LOST  : phiên đã kết thúc
     *  - WINNING / OUTBID : phiên đang chạy
     */
    private String resolveStatus(BidTransaction bt, Auction auction) {
        boolean isHighest = auction.getHighestBid() != null
                && auction.getHighestBid().getId().equals(bt.getId());

        boolean isEnded = switch (auction.getAuctionStatus()) {
            case FINISHED, PAID, CANCELED -> true;
            default -> false;
        };

        if (isEnded) return isHighest ? "WON"     : "LOST";
        else         return isHighest ? "WINNING"  : "OUTBID";
    }

    private String getCurrentUserId() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
