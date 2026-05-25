package Model;

import java.util.ArrayList;
import java.util.List;

public class Seller extends User{
    private List<Item> listItems = new ArrayList<>();
    private double rating;
    private String bankAccount;

    protected Seller(String username, String password, String email, String fullName, String bankAccount) {
        super(username, password, email, fullName);
        this.bankAccount = bankAccount;
        this.rating = 0.0;
    }

    protected List<Item> getListedItems() {
        return listItems;
    }

    protected double getRating() {
        return rating;
    }

    protected String getBankAccount() {
        return bankAccount;
    }

}
