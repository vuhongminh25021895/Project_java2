package View;

import Dto.Response.AuctionCardResponse;
import Scene.SceneManager;
import Scene.SceneName;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class AuctionCardController {
    @FXML
    private Label productNameLabel;

    @FXML
    private Label currentPriceLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView productImage;

    @FXML private VBox root;

    private AuctionCardResponse auctionCardRespone;

    public void setAuction(AuctionCardResponse auctionCardRespone) {
        this.auctionCardRespone = auctionCardRespone;
        productNameLabel.setText(auctionCardRespone.productName());
        currentPriceLabel.setText(String.valueOf(auctionCardRespone.currentPrice()));
        statusLabel.setText(auctionCardRespone.status());
    }

    @FXML
    private void viewDetails() {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionid", auctionCardRespone.auctionId());
        SceneManager.switchTo(SceneName.AUCTION_DETAIL, data);
    }
}
