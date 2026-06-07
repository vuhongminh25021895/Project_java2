package View;

import Dto.Request.BidRequest;
import Dto.Response.BidResponse;
import Scene.SceneManager;
import Service.BidService;
import Service.TimeService;
import Util.AlertBox;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class BidController {

    private BidService bidService = new BidService();

    @FXML
    private TextField bidAmountField;

    @FXML
    private Label currentPriceLabel;

    @FXML
    private Label countdownLabel;

    public void initialize() {
        String auctionId = SceneManager.getData("auctionId");
        BidResponse respone = bidService.getBidDetail(auctionId);
        if (respone.success()) {
            currentPriceLabel.setText(String.valueOf(respone.highestBid()));
            String timeremain = TimeService.getRemainingTime(respone.endtime());
            countdownLabel.setText(timeremain);
        }
    }

    @FXML
    public void placeBid() {
        String auctionId = SceneManager.getData("auctionId");
        BidRequest bidRequest = new BidRequest(auctionId, Double.parseDouble(bidAmountField.getText()));
        BidResponse respone = bidService.placebid(bidRequest);
        if(respone.success()) {
            bidAmountField.setText(String.valueOf(respone.highestBid()));
        }
        else {
            AlertBox.display("Bid false");
        }
    }
}
