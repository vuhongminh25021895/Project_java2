package Model;

import java.time.LocalDateTime;

public abstract class Item extends Entity{
    private String id;
    private String name;
    private String description;
    private double startPrice;
    private String imageUrl;
    private ItemCategory category;
    private Seller seller;
    private double currentHighestBid;
    private LocalDateTime startBid;
    private LocalDateTime endBid;
    private boolean available;

    protected Item() {}

    protected Item(String name, String description, double startingPrice, String imageUrl, Seller seller) {
        this.name = name;
        this.description = description;
        this.startPrice = startingPrice;
        this.imageUrl = imageUrl;
        this.seller = seller;
    }
    @Override
    public String printInfo() {
        return "";
    }

}
