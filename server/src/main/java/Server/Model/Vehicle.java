package Server.Model;

import Server.Enums.ItemCategory;
import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
public class Vehicle extends Item {

    public Vehicle() {
        super();
    }

    @Override
    public ItemCategory getCategory() {
        return ItemCategory.VEHICLE;
    }
}
