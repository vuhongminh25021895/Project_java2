package View;

import Dto.Respone.AuctionCardRespone;
import Scene.SceneManager;
import Scene.SceneName;
import Service.AuctionService;
import Session.ClientSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionListController {
    private final AuctionService auctionService = new AuctionService();

    @FXML private FlowPane auctionContainer;

    @FXML private Label helloName;

    String userId = ClientSession.getInstance().getUserId();
    Map<String, Object> data = new HashMap<>();

    @FXML
    public void initialize() {
        helloName.setText("Xin chào" + ClientSession.getInstance().getUsername());
        loadAuctions();
    }

    public void loadAuctions() {
        List<AuctionCardRespone> auctions = auctionService.getAllAuctions();
        auctionContainer.getChildren().clear();
        for (AuctionCardRespone auction : auctions) {
            addCard(auction);
        }
    }

    private void addCard(AuctionCardRespone auction) {
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
    private void profileSwitch() {
        data.put("userId", userId);
        SceneManager.switchTo(SceneName.PROFILE, data);
    }

    @FXML
    private void myAuctionSwitch() {
        data.put("userId", userId);
        SceneManager.switchTo(SceneName.MY_AUCTIONS, data);
    }

    @FXML
    private void walletSwitch() {
        SceneManager.switchTo(SceneName.DEPOSIT);
    }

    @FXML
    private void logout() {
        ClientSession.getInstance().logout();
        SceneManager.switchTo(SceneName.LOGIN);
    }
}
