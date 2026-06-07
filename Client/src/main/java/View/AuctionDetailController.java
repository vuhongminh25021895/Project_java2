package View;

import Dto.Respone.AuctionDetailRespone;
import Dto.Respone.BidHistoryRespone;
import Scene.SceneManager;
import Scene.SceneName;
import Service.AuctionService;
import Util.AlertBox;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuctionDetailController {
    @FXML private Label titleLabel;

    @FXML private Label statusLabel;

    @FXML private Label currentPriceLabel;

    @FXML private Label bidCountLabel;

    @FXML private Label sellerLabel;

    @FXML private Label startTimeLabel;

    @FXML private Label endTimeLabel;

    @FXML private TextArea descriptionArea;

    @FXML private TableView<BidHistoryRespone> bidHistoryTable;

    @FXML private TableColumn<BidHistoryRespone, String> bidderColumn;

    @FXML private TableColumn<BidHistoryRespone, Double> amountColumn;

    @FXML private TableColumn<BidHistoryRespone, String> timeColumn;

    @FXML private Button bidButton;

    @FXML private Label messageLabel;

    private AuctionService auctionService;

    @FXML public void initialize() {
        loadAuctionData();
        AuctionDetailRespone detailRespone = loadAuctionData();
        statusLabel.setText(detailRespone.status());
        titleLabel.setText(detailRespone.title());
        currentPriceLabel.setText(String.valueOf(detailRespone.bidCount()));
        bidCountLabel.setText(String.valueOf(detailRespone.bidCount()));
        sellerLabel.setText(detailRespone.sellerName());
        startTimeLabel.setText(String.valueOf(detailRespone.startTime()));
        endTimeLabel.setText(String.valueOf(detailRespone.endTime()));
        descriptionArea.setText(detailRespone.description());

    }

    @FXML
    private void openBidScene() {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionid", SceneManager.getData("auctionid"));
        SceneManager.switchTo(SceneName.BIDDING, data);
    }

    private AuctionDetailRespone loadAuctionData() {
        String auctionid = SceneManager.getData("auctionid");
        AuctionDetailRespone detailRespone = auctionService.getDetails(auctionid);
        if (auctionid == null) {
            AlertBox.display("Auction not found");
        }
        if (detailRespone.success() == false) {
            AlertBox.display("Cannot access auction detail");
        }
        return detailRespone;
    }
}
