package app.client.dto.response;

import app.client.enums.ItemCategory;

public class ItemResponse {

    private String id;

    private String name;

    private String description;

    private double startingPrice;

    private String sellerId;

    private ItemCategory category;

    public ItemResponse(
            String id,
            String name,
            String description,
            double startingPrice,
            String sellerId,
            ItemCategory category
    ) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.sellerId = sellerId;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public String getSellerId() {
        return sellerId;
    }

    public ItemCategory getCategory() {
        return category;
    }
}