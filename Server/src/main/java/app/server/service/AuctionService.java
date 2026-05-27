package app.server.service;

import app.server.dto.request.AuctionRequest;
import app.server.dto.response.AuctionResponse;
import app.server.enums.AuctionStatus;
import app.server.enums.UserRole;
import app.server.mapper.AuctionMapper;
import app.server.model.Auction;
import app.server.model.Item;
import app.server.model.Seller;
import app.server.model.User;
import app.server.repository.AuctionRepository;
import app.server.repository.ItemRepository;
import app.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    public AuctionService(
            AuctionRepository auctionRepository,
            ItemRepository itemRepository,
            UserRepository userRepository
    ) {

        this.auctionRepository =
                auctionRepository;

        this.itemRepository =
                itemRepository;

        this.userRepository =
                userRepository;
    }

    public AuctionResponse createAuction(
            AuctionRequest request
    ) {

        User user = userRepository
                .findById(request.getSellerId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Seller not found"
                        )
                );

        if (user.getRole() != UserRole.SELLER) {

            throw new IllegalArgumentException(
                    "User is not seller"
            );
        }

        Seller seller = (Seller) user;

        Item item = itemRepository
                .findById(request.getItemId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item not found"
                        )
                );

        if (!item.getSeller().getId()
                .equals(seller.getId())) {

            throw new IllegalArgumentException(
                    "Seller does not own item"
            );
        }

        if (
                request.getStartTime()
                        .isAfter(request.getEndTime())
        ) {

            throw new IllegalArgumentException(
                    "Invalid auction time"
            );
        }

        if (auctionRepository.existsByItem(item)) {

            throw new IllegalArgumentException(
                    "Item already has auction"
            );
        }

        Auction auction = new Auction(
                item,
                request.getStartTime(),
                request.getEndTime()
        );

        auctionRepository.save(auction);

        return AuctionMapper.toResponse(
                auction
        );
    }

    public List<AuctionResponse>
    getAllAuctions() {

        return auctionRepository
                .findAll()
                .stream()
                .map(AuctionMapper::toResponse)
                .toList();
    }

    public AuctionResponse cancelAuction(
            String auctionId,
            String sellerId
    ) {

        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Auction not found"
                        )
                );

        Seller seller =
                auction.getItem().getSeller();

        if (!seller.getId().equals(sellerId)) {

            throw new IllegalArgumentException(
                    "Only seller can cancel"
            );
        }

        if (
                auction.getAuctionStatus()
                        == AuctionStatus.FINISHED
                        ||
                        auction.getAuctionStatus()
                                == AuctionStatus.CANCELED
        ) {

            throw new IllegalArgumentException(
                    "Auction cannot be canceled"
            );
        }

        auction.setAuctionStatus(
                AuctionStatus.CANCELED
        );

        auctionRepository.save(auction);

        return AuctionMapper.toResponse(
                auction
        );
    }

    @Transactional
    public AuctionResponse joinAuction(
            String auctionId,
            String userId
    ) {

        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Auction not found"
                        )
                );

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        auction.joinAuction(user);

        auctionRepository.save(auction);

        return AuctionMapper.toResponse(auction);
    }

    @Transactional
    public void leaveAuction(
            String auctionId,
            String userId
    ) {

        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Auction not found"
                        )
                );

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        auction.leaveAuction(user);

        auctionRepository.save(auction);
    }
}
