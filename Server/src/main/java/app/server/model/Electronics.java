package app.server.model;

import app.server.enums.ItemCategory;
import jakarta.persistence.Entity;

@Entity
public class Electronics extends Item {

    public Electronics() {
        super();
    }

    @Override
    public ItemCategory getCategory() {
        return ItemCategory.ELECTRONICS;
    }
}
