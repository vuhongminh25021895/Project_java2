package app.server.service;

import app.server.dto.request.ItemRequest;
import app.server.dto.response.ItemResponse;
import app.server.enums.UserRole;
import app.server.factory.ItemFactory;
import app.server.mapper.ItemMapper;
import app.server.model.*;
import app.server.repository.AuctionRepository;
import app.server.repository.ItemRepository;
import app.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final AuctionRepository auctionRepository;

    public ItemService(
            ItemRepository itemRepository,
            UserRepository userRepository,
            AuctionRepository auctionRepository
    ) {

        this.itemRepository = itemRepository;

        this.userRepository = userRepository;

        this.auctionRepository = auctionRepository;
    }

    public ItemResponse createItem(ItemRequest request) {

        User user = userRepository.findById(
                request.getSellerId()
        ).orElseThrow(() ->
                new RuntimeException("Seller not found")
        );

        if (user.getRole() != UserRole.SELLER) {
            throw new RuntimeException("User is not a seller");
        }

        Seller seller = (Seller) user;

        Item item = ItemFactory.createItem(
                request.getCategory()
        );

        item.setName(request.getName());

        item.setDescription(
                request.getDescription()
        );

        item.setStartingPrice(
                request.getStartingPrice()
        );

        item.setSeller(seller);

        Item savedItem = itemRepository.save(item);

        return ItemMapper.toResponse(savedItem);
    }

    public List<ItemResponse> getAllItems() {

        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toResponse)
                .toList();
    }

    public ItemResponse getItemById(String itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found")
                );

        return ItemMapper.toResponse(item);
    }

    public ItemResponse updateItem(
            String itemId,
            ItemRequest request
    ) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found")
                );

        User user = userRepository.findById(
                request.getSellerId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Seller not found"
                )
        );

        if (user.getRole() != UserRole.SELLER) {

            throw new RuntimeException(
                    "User is not a seller"
            );
        }

        Seller seller = (Seller) user;

        if (
                !item.getSeller()
                        .getId()
                        .equals(seller.getId())
        ) {

            throw new RuntimeException(
                    "Only item owner can update item"
            );
        }

        if (auctionRepository.existsByItem(item)) {

            throw new RuntimeException(
                    "Cannot update item with existing auction"
            );
        }

        item.setName(request.getName());

        item.setDescription(
                request.getDescription()
        );

        item.setStartingPrice(
                request.getStartingPrice()
        );

        Item updatedItem = itemRepository.save(item);

        return ItemMapper.toResponse(updatedItem);
    }

    public void deleteItem(
            String itemId,
            String sellerId
    ) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item not found"
                        )
                );

        User user = userRepository.findById(
                sellerId
        ).orElseThrow(() ->
                new RuntimeException(
                        "Seller not found"
                )
        );

        if (user.getRole() != UserRole.SELLER) {

            throw new RuntimeException(
                    "User is not a seller"
            );
        }

        Seller seller = (Seller) user;

        if (
                !item.getSeller()
                        .getId()
                        .equals(seller.getId())
        ) {

            throw new RuntimeException(
                    "Only item owner can delete item"
            );
        }

        if (auctionRepository.existsByItem(item)) {

            throw new RuntimeException(
                    "Cannot delete item with existing auction"
            );
        }

        itemRepository.delete(item);
    }
}