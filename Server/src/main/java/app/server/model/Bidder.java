package app.server.model;

import app.server.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Bidder extends User{

    @OneToMany(
            mappedBy = "bidder",
            fetch = FetchType.LAZY
    )
    private List<BidTransaction> bidHistory = new ArrayList<>();

    public Bidder() {}

    public Bidder(String userName, String password, String fullName) {
        super(userName, password, fullName);
        setRole(UserRole.BIDDER);
    }

    public List<BidTransaction> getBidHistory(){
        return bidHistory;
    }
}
