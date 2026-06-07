package View;

import Dto.Response.BidHistoryResponse;
import Dto.Response.MyItemResponse;
import Dto.Response.ProfileResponse;
import Dto.Response.AuctionCardResponse;
import Service.*;
import Session.ClientSession;
import Util.AlertBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

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

    private ProfileService profileService = new ProfileService();
    private AuctionService auctionService = new AuctionService();
    private BidService bidService = new BidService();
    private ItemService itemService = new ItemService();

    public void initialize() {
        loadProfile();
    }

    private void loadProfile() {
        ProfileResponse respone = profileService.getprofiledetail(ClientSession.getInstance().getUserId());
        if (respone.success()) {
            fullnameLabel.setText(respone.fullname());
            usernameLabel.setText(respone.username());
            emailLabel.setText(respone.email());
            participatedLabel.setText(String.valueOf(respone.participatedAuctions()));
            wonLabel.setText(String.valueOf(respone.wonAuctions()));
            revenueLabel.setText(String.valueOf(respone.revenue()));
            productsLabel.setText(String.valueOf(respone.productscount()));
        }
        else {
            AlertBox.display("Lấy thông tin thất bại");
        }
    }

    @FXML
    private void showMyAuctions() { String userId = ClientSession.getInstance().getUserId();
        if (userId == null) { AlertBox.display("Người dùng không tồn tại hoặc token đã hết hạn. Vui lòng đăng nhập lại"); }
        List<AuctionCardResponse> lst = auctionService.getAllMyAuctions(userId);
        List<AuctionRow> lstauctionrow = new ArrayList<>();
        for (AuctionCardResponse cardResponse: lst) {
            if (!cardResponse.success()) {
                AlertBox.display("Lấy thông tin thất bại"); return; }
            String timeremain = TimeService.getRemainingTime(cardResponse.endTime());
            lstauctionrow.add(new AuctionRow(cardResponse.productName(), cardResponse.currentPrice(), timeremain, cardResponse.status())); }
        dataTable.setItems(FXCollections.observableArrayList(lstauctionrow));
    }

    @FXML
    private void showBidHistory() {
        String userId = ClientSession.getInstance().getUserId();

        if (userId == null) {
            AlertBox.display("Người dùng không tồn tại hoặc token đã hết hạn. Vui lòng đăng nhập lại");
            return;
        }

        List<BidHistoryResponse> lst = bidService.getMyBidHistory(userId);
        List<AuctionRow> rows = new ArrayList<>();

        for (BidHistoryResponse response : lst) {

            if (!response.success()) {
                AlertBox.display("Lấy lịch sử đấu giá thất bại");
                return;
            }

            String bidtime = TimeService.format(response.bidTime());

            rows.add(
                    new AuctionRow(
                            response.biddername(),
                            response.amount(),
                            bidtime,
                            response.status()
                    )
            );
        }

        dataTable.setItems(FXCollections.observableArrayList(rows));
    }


    @FXML
    private void showMyProducts() {
        String userId = ClientSession.getInstance().getUserId();

        if (userId == null) {
            AlertBox.display("Người dùng không tồn tại hoặc token đã hết hạn. Vui lòng đăng nhập lại");
            return;
        }

        List<MyItemResponse> lst = itemService.getMyItems(userId);
        List<AuctionRow> rows = new ArrayList<>();

        for (MyItemResponse response : lst) {

            if (!response.success()) {
                AlertBox.display("Lấy danh sách sản phẩm thất bại");
                return;
            }

            String timeremain = TimeService.getRemainingTime(response.endTime());

            rows.add(
                    new AuctionRow(
                            response.productName(),
                            response.currentPrice(),
                            timeremain,
                            response.status()
                    )
            );
        }

        dataTable.setItems(FXCollections.observableArrayList(rows));
    }
}
