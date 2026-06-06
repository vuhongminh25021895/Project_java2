package View;

import Dto.Respone.AuctionCardRespone;
import Scene.SceneManager;
import Scene.SceneName;
import Service.AuctionService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    private AuctionCardRespone auction;

    public void setAuction(AuctionCardRespone auction) {
        this.auction = auction;
        productNameLabel.setText(auction.productName());
        currentPriceLabel.setText(String.valueOf(auction.currentPrice()));
        statusLabel.setText(auction.status());
        loadImage(auction.imageUrl());
    }

    private void loadImage(String url) {
        if (url == null || url.isEmpty()) return;
        Image image = new Image(url, true);
        productImage.setImage(image);
    }

    @FXML
    private void viewDetails() {
        Map<String, Object> data = new HashMap<>();
        data.put("auctionid", auction.auctionId());
        SceneManager.switchTo(SceneName.AUCTION_DETAIL, data);
    }
}
