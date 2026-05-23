package View;

import Dto.AuctionProductItem;
import Service.AuctionProductService;
import StageManager.SceneName;
import StageManager.SwitchSceneControll;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class AuctionListController {
    private static String currentUsername;
    private final AuctionProductService auctionProductService = new AuctionProductService();

    @FXML private MenuButton HelloName;
    @FXML private TableView<AuctionProductItem> tableSanPhams;
    @FXML private TableColumn<AuctionProductItem, String> colTen;
    @FXML private TableColumn<AuctionProductItem, String> colTenSanPham;
    @FXML private TableColumn<AuctionProductItem, Integer> colGiaHienTai;
    @FXML private TableColumn<AuctionProductItem, Integer> colGiaMuaDut;
    @FXML private TableColumn<AuctionProductItem, String> colThoiDiemKetThuc;
    @FXML private TableColumn<AuctionProductItem, String> Status;

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
    public static String getCurrentUsername() {
        return currentUsername;
    }

    @FXML
    public void initialize() {
        String displayName = (currentUsername == null || currentUsername.isBlank()) ? "Guest" : currentUsername;
        HelloName.setText("Hello, " + displayName);
        configureColumns();
        loadProducts();
    }

    @FXML
    public void ProductPublishingSwitch(ActionEvent event){
        try {
            SwitchSceneControll.switchScene(SceneName.PRODUCT_PUBLISHING);
        } catch (Exception e) {
            AlertBox.display("Không thể mở màn hình.");
        }
    }
    @FXML
    public void MainSceneSwitch(ActionEvent event){
        currentUsername = null;
        try {
            SwitchSceneControll.switchScene(SceneName.LOGIN);
        } catch (Exception e) {
            AlertBox.display("Không thể mở màn hình.");
        }
    }

    private void configureColumns() {
        colTen.setCellValueFactory(new PropertyValueFactory<>("category"));
        colTenSanPham.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colGiaHienTai.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        colGiaMuaDut.setCellValueFactory(new PropertyValueFactory<>("buyNowPrice"));
        colThoiDiemKetThuc.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        Status.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadProducts() {
        try {
            tableSanPhams.setItems(FXCollections.observableArrayList(auctionProductService.fetchProducts()));
        } catch (IOException e) {
            tableSanPhams.setItems(FXCollections.observableArrayList());
            AlertBox.display("Không thể tải danh sách đấu giá từ server.");
        }
    }
}
