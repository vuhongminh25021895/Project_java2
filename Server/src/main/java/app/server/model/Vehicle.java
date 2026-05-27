package app.server.model;

import app.server.enums.ItemCategory;
import jakarta.persistence.Entity;

@Entity
public class Vehicle extends Item {

    public Vehicle() {
        super();
    }

    @Override
    public ItemCategory getCategory() {
        return ItemCategory.VEHICLE;
    }
}