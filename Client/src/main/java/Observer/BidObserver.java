package Observer;

import Dto.Response.BidResponse;

public interface BidObserver {
    void onNewBid(BidResponse bid);

}
