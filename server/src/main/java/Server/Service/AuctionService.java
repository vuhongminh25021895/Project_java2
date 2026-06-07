package Server.Service;

import Server.Dto.Respone.AuctionCardRespone;
import Server.Dto.Respone.AuctionDetailRespone;
import Server.Dto.Respone.BidHistoryRespone;
import Server.Exception.AuctionNotfoundException;
import Server.Mapper.AuctionCardMapper;
import Server.Model.Auction;
import Server.Model.BidTransaction;
import Server.Repository.AuctionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
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
            bidHistoryRespones.add(new BidHistoryRespone(bid.getBidder().getUserName(), bid.getBidAmount(), TimeService.getTime(bid.getBidTime())));
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

