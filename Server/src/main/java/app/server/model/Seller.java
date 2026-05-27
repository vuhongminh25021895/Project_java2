package app.server.model;

import app.server.enums.UserRole;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Seller extends User{

    @OneToMany(
            mappedBy = "seller",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Item> listItems = new ArrayList<>();

    public Seller() {}

    public Seller(String username, String password, String fullName) {
        super(username, password, fullName);
        setRole(UserRole.SELLER);
    }

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
