package View;

import Dto.AuctionProductItem;
import Dto.BidUpdateEvent;
import Dto.DepositResponse;
import Service.AuctionProductService;
import Service.BidRealtimeService;
import Service.DepositService;
import StageManager.SceneManager;
import StageManager.SceneName;
import StageManager.SwitchSceneControll;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

public class AuctionListController {
    private static final Locale VIETNAM = Locale.forLanguageTag("vi-VN");
    private static String currentUsername;
    private static final LongProperty currentBalance = new SimpleLongProperty(0L);

    private final AuctionProductService auctionProductService = new AuctionProductService();
    private final DepositService depositService = new DepositService();
    private final BidRealtimeService bidRealtimeService = BidRealtimeService.getInstance();
    private final BidRealtimeService.BidUpdateListener bidUpdateListener = this::handleBidUpdate;
    private Timeline countdownRefreshTimeline;

    @FXML private MenuButton HelloName;
    @FXML private Label lblCurrentBalance;
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

    public static void setCurrentBalance(Long balance) {
        currentBalance.set(balance == null ? 0L : Math.max(balance, 0L));
    }

    public static long getCurrentBalance() {
        return currentBalance.get();
    }

    @FXML
    public void initialize() {
        String displayName = (currentUsername == null || currentUsername.isBlank()) ? "Guest" : currentUsername;
        HelloName.setText("Hello, " + displayName);
        bindCurrentBalanceLabel();
        refreshCurrentBalance();
        configureColumns();
        configureProductSelection();
        loadProducts();
        bidRealtimeService.addListener(bidUpdateListener);
        startCountdownRefresh();
    }

    @FXML
    public void ProductPublishingSwitch(ActionEvent event) {
        dispose();
        stopCountdownRefresh();
        try {
            SwitchSceneControll.switchScene(SceneName.PRODUCT_PUBLISHING);
        } catch (Exception e) {
            AlertBox.display("Không thể mở màn hình.");
        }
    }

    @FXML
    public void MainSceneSwitch(ActionEvent event) {
        currentUsername = null;
        setCurrentBalance(0L);
        dispose();
        stopCountdownRefresh();
        try {
            SwitchSceneControll.switchScene(SceneName.LOGIN);
        } catch (Exception e) {
            AlertBox.display("Không thể mở màn hình.");
        }
    }

    @FXML
    public void NapTienSwitch(ActionEvent event) {
        dispose();
        stopCountdownRefresh();
        try {
            SwitchSceneControll.switchScene(SceneName.NAP_TIEN);
        } catch (Exception e) {
            AlertBox.display("Không thể mở màn hình nạp tiền.");
        }
    }

    private void bindCurrentBalanceLabel() {
        lblCurrentBalance.textProperty().bind(Bindings.createStringBinding(
                () -> "Số dư: " + formatCurrency(currentBalance.get()) + " VNĐ",
                currentBalance
        ));
    }

    private void refreshCurrentBalance() {
        if (currentUsername == null || currentUsername.isBlank()) {
            setCurrentBalance(0L);
            return;
        }

        try {
            DepositResponse response = depositService.getBalance(currentUsername);
            if (response != null && response.isSuccess() && response.getNewBalance() != null) {
                setCurrentBalance(response.getNewBalance());
            }
        } catch (Exception ignored) {
        }
    }

    private void configureColumns() {
        colTen.setCellValueFactory(new PropertyValueFactory<>("category"));
        colTenSanPham.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colGiaHienTai.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        colGiaMuaDut.setCellValueFactory(new PropertyValueFactory<>("buyNowPrice"));
        colThoiDiemKetThuc.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(formatCountdown(cellData.getValue().getEndTime())));
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

    private void configureProductSelection() {
        tableSanPhams.setRowFactory(tableView -> {
            TableRow<AuctionProductItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    openBiddingPopup(row.getItem());
                }
            });
            return row;
        });
    }

    private void openBiddingPopup(AuctionProductItem product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BiddingScene.fxml"));
            Scene scene = new Scene(loader.load());

            BiddingController controller = loader.getController();
            controller.setProduct(product);

            Stage dialog = new Stage();
            dialog.setOnHidden(event -> controller.dispose());
            dialog.initOwner(SceneManager.getStage());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);
            dialog.setTitle("Đặt giá sản phẩm");
            dialog.setScene(scene);
            dialog.showAndWait();

            loadProducts();
        } catch (IOException e) {
            AlertBox.display("Không thể mở màn hình đặt giá.");
        }
    }

    private void startCountdownRefresh() {
        countdownRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> tableSanPhams.refresh()));
        countdownRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownRefreshTimeline.play();
    }

    private void stopCountdownRefresh() {
        if (countdownRefreshTimeline != null) {
            countdownRefreshTimeline.stop();
        }
    }

    private String formatCountdown(String endTime) {
        try {
            LocalDateTime endDateTime = LocalDateTime.parse(endTime);
            java.time.Duration remaining = java.time.Duration.between(LocalDateTime.now(), endDateTime);
            if (remaining.isNegative() || remaining.isZero()) {
                return "0d 00h 00m";
            }

            long totalMinutes = remaining.toMinutes();
            long days = totalMinutes / (24 * 60);
            long hours = (totalMinutes % (24 * 60)) / 60;
            long minutes = totalMinutes % 60;

            return String.format("%dd %02dh %02dm", days, hours, minutes);
        } catch (Exception e) {
            return "N/A";
        }
    }

    private void handleBidUpdate(BidUpdateEvent event) {
        if (event == null || event.getProductId() == null) {
            return;
        }

        Platform.runLater(() -> {
            for (AuctionProductItem item : tableSanPhams.getItems()) {
                if (item.getId() == event.getProductId()) {
                    if (event.getCurrentPrice() != null) {
                        item.setCurrentPrice(event.getCurrentPrice());
                    }
                    if (event.getStatus() != null && !event.getStatus().isBlank()) {
                        item.setStatus(event.getStatus());
                    }
                    tableSanPhams.refresh();
                    syncBalanceFromBidEvent(event);
                    return;
                }
            }

            syncBalanceFromBidEvent(event);
        });
    }

    private void syncBalanceFromBidEvent(BidUpdateEvent event) {
        String username = currentUsername;
        if (username == null || username.isBlank()) {
            return;
        }

        if (username.equals(event.getPreviousHighestBidderUsername()) && event.getPreviousHighestBidderBalance() != null) {
            setCurrentBalance(event.getPreviousHighestBidderBalance());
        }

        if (username.equals(event.getHighestBidderUsername()) && event.getHighestBidderBalance() != null) {
            setCurrentBalance(event.getHighestBidderBalance());
        }
    }

    private void dispose() {
        bidRealtimeService.removeListener(bidUpdateListener);
    }

    private String formatCurrency(long amount) {
        return NumberFormat.getInstance(VIETNAM).format(amount);
    }
}
