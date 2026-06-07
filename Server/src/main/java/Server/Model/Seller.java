package Server.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Seller extends User{
    @OneToMany(
            mappedBy = "seller",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Item> listItems = new ArrayList<>();

    public void addItem(Item item) {
        listItems.add(item);
        item.setSeller(this);
    }

    public void removeItem(Item item) {
        listItems.remove(item);
        item.setSeller(null);
    }

    public List<Item> getListItems() {
        return listItems;
    }
}

