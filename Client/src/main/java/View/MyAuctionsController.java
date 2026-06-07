package View;

import Dto.Response.AuctionCardResponse;
import Scene.SceneManager;
import Scene.SceneName;
import Service.AuctionService;
import Session.ClientSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAuctionsController {
    @FXML
    private FlowPane auctionContainer;

    private final AuctionService auctionService = new AuctionService();

    @FXML
    public void initialize() {
        loadAuctions();
    }
    public void loadAuctions() {
        String userId = SceneManager.getData("userId");
        List<AuctionCardResponse> auctions = auctionService.getAllMyAuctions(userId);
        auctionContainer.getChildren().clear();
        for (AuctionCardResponse auction : auctions) {
            addCard(auction);
        }
    }

    private void addCard(AuctionCardResponse auction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auctioncard.fxml"));
            VBox card = loader.load();
            AuctionCardController controller = loader.getController();
            controller.setAuction(auction);
            auctionContainer.getChildren().add(card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void back() {
        String userId = ClientSession.getInstance().getUserId();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        SceneManager.switchTo(SceneName.AUCTION_LIST, data);
    }
}