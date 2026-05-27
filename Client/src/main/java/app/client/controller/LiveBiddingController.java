package app.client.controller;

import app.client.dto.response.BidResponse;
import app.client.websocket.AuctionSocketClient;
import app.client.websocket.BidUpdateListener;

public class LiveBiddingController
        implements BidUpdateListener {

    private final AuctionSocketClient socketClient;

    public LiveBiddingController() throws Exception {

        socketClient = new AuctionSocketClient();

        socketClient.connect();

        socketClient.setListener(this);
    }

    public void watchAuction(
            String auctionId
    ) {

        socketClient.subscribeAuction(auctionId);
    }

    @Override
    public void onBidUpdated(
            BidResponse response
    ) {
        // Add JavaFX GUI man hinh dau gia truc tuyen
        // TODO:
        // Update highest bid label

        // TODO:
        // Update highest bidder label

        // TODO:
        // Add new bid to bid history table

        // TODO:
        // Update realtime line chart

        // TODO:
        // Refresh remaining time if needed
    }
}
