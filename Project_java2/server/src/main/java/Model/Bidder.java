package Model;

import java.util.List;

public class Bidder extends User{
    private double balance;
    private List<String> watchlist;
    private List<BidTransaction> bidHistory;

    protected Bidder(String userName, String password, String email, String fullName) {
        super( userName, password, email, fullName);
    }

    protected List<String> getWatchlist(){
        return watchlist;
    }

    protected List<BidTransaction> getBidHistory(){
        return bidHistory;
    }

    public double getBalance() {
        return balance;
    }

    public void reduce(double amount) {
        balance -= amount;
    }
}
