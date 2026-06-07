package Server.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Bidder extends User{

    @OneToMany(
            mappedBy = "bidder",
            fetch = FetchType.LAZY
    )
    private List<BidTransaction> bidHistory = new ArrayList<>();

    public List<BidTransaction> getBidHistory(){
        return bidHistory;
    }
}
