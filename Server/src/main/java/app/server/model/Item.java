package app.server.model;

import app.shared.enums.ItemCategory;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item extends BaseEntity {
    private String name;
    private String description;
    private double startingPrice;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private ItemCategory category;
    @ManyToOne
    private Seller seller;

    public Item() {}

    protected Item(String name, String description, double startingPrice, String imageUrl, Seller seller) {
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.imageUrl = imageUrl;
        this.seller = seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }
}
