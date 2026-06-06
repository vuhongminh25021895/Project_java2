package View;

import Dto.Request.BidRequest;
import Scene.SceneManager;
import Service.BidService;
import javafx.fxml.FXML;

import java.awt.*;

public class BidController {

    private BidService bidService;

    @FXML
    private TextField bidAmountField;

    public void initialize() {
        String auctionId = SceneManager.getData("auctionId");
        bidService.getBidDetail(auctionId);
    }

    @FXML
    public void placeBid() {
        String auctionId = SceneManager.getData("auctionId");
        BidRequest bidRequest = new BidRequest(auctionId, Double.parseDouble(bidAmountField.getText()));
        bidService.placebid(bidRequest);

    }
}
