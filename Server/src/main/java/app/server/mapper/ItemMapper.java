package app.server.mapper;

import app.server.dto.response.ItemResponse;
import app.server.model.Item;

public class ItemMapper {

    public static ItemResponse toResponse(
            Item item
    ) {

        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getStartingPrice(),
                item.getSeller().getId(),
                item.getCategory()
        );
    }
}