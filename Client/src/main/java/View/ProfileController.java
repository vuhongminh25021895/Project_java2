package View;

import Dto.Respone.ProfileRespone;
import Service.ProfileService;
import Session.ClientSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ProfileController {
    @FXML private Label fullnameLabel;

    @FXML private Label usernameLabel;

    @FXML private Label emailLabel;

    @FXML private Label memberSinceLabel;

    @FXML private Label participatedLabel;

    @FXML private Label wonLabel;

    @FXML private Label revenueLabel;

    @FXML private Label productsLabel;

    @FXML private Button myAuctionsBtn;

    @FXML private Button bidHistoryBtn;

    @FXML private Button wonAuctionsBtn;

    @FXML private Button myProductsBtn;

    @FXML private Label sectionTitle;

    @FXML private TableView<Object> dataTable;

    @FXML private TableColumn<Object, String> col1;

    @FXML private TableColumn<Object, String> col2;

    @FXML private TableColumn<Object, String> col3;

    @FXML private TableColumn<Object, String> col4;

    private ProfileService profileService;

    public void initialize() {
        loadProfile();
    }

    private void loadProfile() {
        ProfileRespone respone = profileService.getprofiledetail(ClientSession.getInstance().getUserId());
        fullnameLabel.setText(respone.fullname());
        usernameLabel.setText(respone.username());
        emailLabel.setText(respone.email());
        participatedLabel.setText(String.valueOf(respone.participatedAuctions()));
        wonLabel.setText(String.valueOf(respone.wonAuctions()));
        revenueLabel.setText(String.valueOf(respone.revenue()));
        productsLabel.setText(String.valueOf(respone.productscount()));
    }

    @FXML
    private void showMyAuctions() {}

    @FXML
    private void showBidHistory() {}

    @FXML
    private void showWonAuctions() {}

    @FXML
    private void showMyProducts() {}

}
