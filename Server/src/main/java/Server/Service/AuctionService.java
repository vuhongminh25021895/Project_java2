package Server.Service;

import Server.Dto.Respone.AuctionCardRespone;
import Server.Dto.Respone.AuctionDetailRespone;
import Server.Dto.Respone.BidHistoryRespone;
import Server.Dto.Respone.MyItemResponse;
import Server.Exception.AuctionNotfoundException;
import Server.Exception.UserNotFoundException;
import Server.Mapper.AuctionCardMapper;
import Server.Model.*;
import Server.Repository.AuctionRepository;
import Server.Repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public AuctionService(AuctionRepository auctionRepository, UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
    }

    public List<AuctionCardRespone> getAllAuctions() {
        return auctionRepository.findAll()
                .stream()
                .map(AuctionCardMapper::toCard)
                .toList();
    }

    public AuctionDetailRespone getDetail(String auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(
                        () -> new AuctionNotfoundException(auctionId)
                );
        String highestBidderName = null;
        BigDecimal currentPrice = BigDecimal.ZERO;
        if (auction.getHighestBid() != null) {

            currentPrice =
                    auction.getHighestBid()
                            .getBidAmount();

            highestBidderName =
                    auction.getHighestBid()
                            .getBidder()
                            .getFullName();
        } else {
            currentPrice = auction.getItem().getStartingPrice();
        }
        List<BidTransaction> lst = auction.getBidHistory();
        List<BidHistoryRespone> bidHistoryRespones = new ArrayList<>();
        for (BidTransaction bid: lst) {
            bidHistoryRespones.add(new BidHistoryRespone(true, bid.getBidder().getUserName(), bid.getBidAmount(), bid.getBidTime(), String.valueOf(bid.getAuction().getAuctionStatus())));
        }
        return new AuctionDetailRespone(true,
                auctionId,
                auction.getItem().getName(),
                auction.getItem().getDescription(),
                currentPrice,
                auction.getBidHistory().size(),
                auction.getItem().getSeller().getUserName(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getAuctionStatus().name(),
                highestBidderName,
                bidHistoryRespones);
    }

}

