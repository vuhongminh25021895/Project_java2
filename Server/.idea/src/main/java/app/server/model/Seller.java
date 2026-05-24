package app.server.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Seller extends User{
    @OneToMany(mappedBy = "seller")
    private List<Item> listItems = new ArrayList<>();
    private double rating;
    private String bankAccount;

    public Seller() {}

    public Seller(String username, String password, String email, String fullName, String bankAccount) {
        super(username, password, email, fullName);
        this.bankAccount = bankAccount;
        this.rating = 0.0;
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

    protected double getRating() {
        return rating;
    }

    protected String getBankAccount() {
        return bankAccount;
    }
}
