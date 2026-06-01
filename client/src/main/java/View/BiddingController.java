package View;

import Dto.AuctionProductItem;
import Dto.AuctionProductResponse;
import Dto.BidHistoryPoint;
import Dto.BidRequest;
import Dto.BidUpdateEvent;
import Service.AuctionProductService;
import Service.BidRealtimeService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BiddingController {
    private static final int MAX_CHART_POINTS = 10;
    private static final DateTimeFormatter CHART_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final AuctionProductService auctionProductService = new AuctionProductService();
    private final BidRealtimeService bidRealtimeService = BidRealtimeService.getInstance();
    private final BidRealtimeService.BidUpdateListener bidUpdateListener = this::handleBidUpdate;
    private final XYChart.Series<String, Number> priceSeries = new XYChart.Series<>();
    private AuctionProductItem product;
    private String latestAppliedBidTime;
    private boolean realtimeSubscribed;

    @FXML private Text lblTenSanPham;
    @FXML private TextArea txtMoTa;
    @FXML private LineChart<String, Number> priceHistoryChart;
    @FXML private Label lblGiaHienTai;
    @FXML private Label lblGiaChot;
    @FXML private Label lblGiaToiThieu;
    @FXML private Button btnGiaToiThieu;
    @FXML private Button btnDangGia;
    @FXML private TextField txtGiaMoi;

    public void setProduct(AuctionProductItem product) {
        this.product = product;
        configureChart();
        populateProductDetails();
        loadBidHistory();
        updateBidControls();
        subscribeToRealtimeUpdates();
    }

    @FXML
    public void sendMinimumBid(ActionEvent event) {
        if (product == null) {
            AlertBox.display("Không tìm thấy sản phẩm để đặt giá.");
            return;
        }
        submitBid(calculateMinimumBid(product.getCurrentPrice()));
    }

    @FXML
    public void sendCustomBid(ActionEvent event) {
        if (product == null) {
            AlertBox.display("Không tìm thấy sản phẩm để đặt giá.");
            return;
        }

        String bidText = txtGiaMoi.getText().trim();
        if (!isInt(bidText)) {
            AlertBox.display("Giá mới phải là số nguyên hợp lệ.");
            return;
        }

        submitBid(Integer.parseInt(bidText));
    }

    private void populateProductDetails() {
        if (product == null) {
            return;
        }

        lblTenSanPham.setText(product.getProductName());
        txtMoTa.setText(product.getDescription() == null || product.getDescription().isBlank()
                ? "Sản phẩm chưa có mô tả."
                : product.getDescription());
        lblGiaChot.setText("Giá chốt: " + product.getBuyNowPrice());
        refreshPriceDetails(false);
    }

    private void submitBid(int bidAmount) {
        if (bidAmount <= 0) {
            AlertBox.display("Giá mới phải lớn hơn 0.");
            return;
        }

        if (product != null
                && AuctionListController.getCurrentUsername() != null
                && AuctionListController.getCurrentUsername().equals(product.getSellerUsername())) {
            AlertBox.display("Bạn không thể đặt giá cho sản phẩm của chính mình.");
            return;
        }

        int minimumBid = calculateMinimumBid(product.getCurrentPrice());
        if (bidAmount < minimumBid) {
            AlertBox.display("Giá đặt phải lớn hơn hoặc bằng " + minimumBid + ".");
            return;
        }

        String bidderUsername = AuctionListController.getCurrentUsername();
        if (bidderUsername == null || bidderUsername.isBlank() || "Guest".equalsIgnoreCase(bidderUsername)) {
            AlertBox.display("Vui lòng đăng nhập trước khi đặt giá.");
            return;
        }

        AuctionProductResponse response = auctionProductService.placeBid(
                product.getId(),
                new BidRequest(bidderUsername, bidAmount)
        );

        AlertBox.display(response.getMessage());
        if (response.isSuccess()) {
            if (response.getNewBalance() != null) {
                AuctionListController.setCurrentBalance(response.getNewBalance());
            }
            applySuccessfulBid(response);
        }
    }

    private void configureChart() {
        priceHistoryChart.setAnimated(false);
        if (priceHistoryChart.getData().isEmpty()) {
            priceSeries.setName("Giá");
            priceHistoryChart.getData().add(priceSeries);
        }
    }

    private void loadBidHistory() {
        try {
            renderBidHistory(auctionProductService.fetchBidHistory(product.getId()));
        } catch (IOException e) {
            priceSeries.getData().clear();
            AlertBox.display("Không thể tải lịch sử đặt giá.");
        }
    }

    private void renderBidHistory(List<BidHistoryPoint> history) {
        priceSeries.getData().clear();
        int startIndex = Math.max(0, history.size() - MAX_CHART_POINTS);
        for (int index = startIndex; index < history.size(); index++) {
            BidHistoryPoint point = history.get(index);
            appendBidHistoryPoint(point);
        }
        latestAppliedBidTime = history.isEmpty() ? null : history.get(history.size() - 1).getTime();
    }

    private void appendBidHistoryPoint(BidHistoryPoint point) {
        if (point == null || point.getPrice() == null || point.getTime() == null || point.getTime().isBlank()) {
            return;
        }

        priceSeries.getData().add(new XYChart.Data<>(formatChartTime(point.getTime()), point.getPrice()));
        while (priceSeries.getData().size() > MAX_CHART_POINTS) {
            priceSeries.getData().remove(0);
        }
    }

    private void applySuccessfulBid(AuctionProductResponse response) {
        Integer currentPrice = response.getCurrentPrice();
        if (currentPrice == null && isInt(txtGiaMoi.getText().trim())) {
            currentPrice = Integer.parseInt(txtGiaMoi.getText().trim());
        }

        applyBidUpdate(currentPrice, response.getStatus(), response.getBidTime(), response.getEndTime());
    }

    private void updateBidControls() {
        boolean isOpen = product != null && "OPEN".equalsIgnoreCase(product.getStatus());
        boolean isOwnProduct = product != null
                && AuctionListController.getCurrentUsername() != null
                && AuctionListController.getCurrentUsername().equals(product.getSellerUsername());
        boolean canBid = isOpen && !isOwnProduct;

        btnGiaToiThieu.setDisable(!canBid);
        btnDangGia.setDisable(!canBid);
        txtGiaMoi.setDisable(!canBid);

        if (!isOpen) {
            txtGiaMoi.clear();
            lblGiaToiThieu.setText("Phiên đấu giá đã kết thúc.");
            return;
        }

        if (isOwnProduct) {
            txtGiaMoi.clear();
            lblGiaToiThieu.setText("Bạn không thể đặt giá cho sản phẩm của chính mình.");
        }
    }

    private void subscribeToRealtimeUpdates() {
        if (realtimeSubscribed) {
            return;
        }

        bidRealtimeService.addListener(bidUpdateListener);
        realtimeSubscribed = true;
    }

    public void dispose() {
        if (!realtimeSubscribed) {
            return;
        }

        bidRealtimeService.removeListener(bidUpdateListener);
        realtimeSubscribed = false;
    }

    private void handleBidUpdate(BidUpdateEvent event) {
        if (product == null || event == null || event.getProductId() == null || event.getProductId() != product.getId()) {
            return;
        }

        Platform.runLater(() -> applyBidUpdate(event.getCurrentPrice(), event.getStatus(), event.getBidTime(), event.getEndTime()));
    }

    private void applyBidUpdate(Integer currentPrice, String status, String bidTime, String endTime) {
        if (bidTime != null && bidTime.equals(latestAppliedBidTime)) {
            return;
        }

        if (currentPrice != null) {
            product.setCurrentPrice(currentPrice);
        }
        if (status != null && !status.isBlank()) {
            product.setStatus(status);
        }
        if (endTime != null && !endTime.isBlank()) {
            product.setEndTime(endTime);
        }

        refreshPriceDetails(true);
        if (bidTime != null && currentPrice != null) {
            appendBidHistoryPoint(new BidHistoryPoint(currentPrice, bidTime));
            latestAppliedBidTime = bidTime;
        }
        updateBidControls();
    }

    private void refreshPriceDetails(boolean preserveInput) {
        int minimumBid = calculateMinimumBid(product.getCurrentPrice());

        lblGiaHienTai.setText("Giá hiện tại: " + product.getCurrentPrice());
        lblGiaToiThieu.setText("Giá nhỏ nhất = " + minimumBid);

        if (!preserveInput) {
            txtGiaMoi.setText(String.valueOf(minimumBid));
            return;
        }

        String currentInput = txtGiaMoi.getText().trim();
        if (!isInt(currentInput) || Integer.parseInt(currentInput) < minimumBid) {
            txtGiaMoi.setText(String.valueOf(minimumBid));
        }
    }

    private int calculateMinimumBid(int currentPrice) {
        return (int) Math.ceil(currentPrice * 1.02d);
    }

    private boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String formatChartTime(String rawTime) {
        try {
            return LocalDateTime.parse(rawTime).format(CHART_TIME_FORMATTER);
        } catch (Exception e) {
            return rawTime;
        }
    }
}
