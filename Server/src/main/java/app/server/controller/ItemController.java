package app.server.controller;

import app.server.dto.request.ItemRequest;
import app.server.dto.response.ItemResponse;
import app.server.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(
            ItemService itemService
    ) {

        this.itemService = itemService;
    }

    @PostMapping
    public ItemResponse createItem(
            @RequestBody ItemRequest request
    ) {

        return itemService.createItem(request);
    }

    @GetMapping
    public List<ItemResponse> getAllItems() {

        return itemService.getAllItems();
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(
            @PathVariable String itemId
    ) {

        return itemService.getItemById(itemId);
    }

    @PutMapping("/{itemId}")
    public ItemResponse updateItem(
            @PathVariable String itemId,
            @RequestBody ItemRequest request
    ) {

        return itemService.updateItem(
                itemId,
                request
        );
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @PathVariable String itemId,
            @RequestParam String sellerId
    ) {

        itemService.deleteItem(
                itemId,
                sellerId
        );
    }
}
