package View;


import Dto.Respone.AuctionCardRespone;
import Service.AuctionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class AuctionListController {
    private final AuctionService auctionService = new AuctionService();

    @FXML private FlowPane auctionContainer;

    @FXML private MenuButton helloName;

    @FXML
    public void initialize() {
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
}



