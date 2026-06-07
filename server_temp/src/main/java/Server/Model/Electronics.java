package Server.Model;

import Server.Enums.ItemCategory;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@SuperBuilder
public class Electronics extends Item {

    @Override
    public ItemCategory getCategory() {
        return ItemCategory.ELECTRONICS;
    }
}
