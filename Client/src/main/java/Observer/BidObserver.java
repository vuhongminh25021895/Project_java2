package Observer;

import Dto.Respone.BidResponse;

public interface BidObserver {
    void onNewBid(BidResponse bid);
}
