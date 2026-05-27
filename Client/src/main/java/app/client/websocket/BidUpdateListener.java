package app.client.websocket;

import app.client.dto.response.BidResponse;

public interface BidUpdateListener {

    void onBidUpdated(BidResponse response);
}
