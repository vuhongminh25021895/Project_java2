package app.server.model;

import app.server.enums.ItemCategory;
import jakarta.persistence.Entity;

@Entity
public class Art extends Item {

    public Art() {
        super();
    }

    @Override
    public ItemCategory getCategory() {
        return ItemCategory.ART;
    }
}