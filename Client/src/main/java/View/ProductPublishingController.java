package View;

import Config.ApiConfig;
import Dto.Request.ProducPublishRequest;
import Dto.Response.ProductPublishResponse;
import Scene.SceneManager;
import Scene.SceneName;
import Service.ItemService;
import Service.ItemService;
import Session.ClientSession;
import Util.AlertBox;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductPublishingController {

    // ── Panel trái ──────────────────────────────────────────
    @FXML private Label UserName;

    // ── Panel phải – form nhập liệu ─────────────────────────
    @FXML private TextField     ProductName;
    @FXML private ChoiceBox<String> CategoryChoice;
    @FXML private TextArea      ProductDescription;
    @FXML private TextField     InitPrice;
    @FXML private TextField     IAPrice;
    @FXML private DatePicker    EndDate;
    @FXML private ChoiceBox<Integer> EndHour;

    private final ItemService itemService = new ItemService();

    // ────────────────────────────────────────────────────────
    // initialize – chạy ngay sau khi FXML được nạp
    // ────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Hiển thị tên người dùng đang đăng nhập
        String username = ClientSession.getInstance().getUsername();
        UserName.setText(username != null ? username : "Khách");

        // Nạp danh sách giờ kết thúc (0 – 23)
        for (int h = 0; h <= 23; h++) {
            EndHour.getItems().add(h);
        }
        EndHour.setValue(0);

        // Đặt giá trị mặc định cho ngày kết thúc là hôm nay + 7 ngày
        EndDate.setValue(LocalDate.now().plusDays(7));
    }

    // ────────────────────────────────────────────────────────
    // SendAuction – xử lý khi nhấn nút "Đăng sản phẩm"
    // ────────────────────────────────────────────────────────
    @FXML
    private void SendAuction() {

        // 1. Lấy & kiểm tra dữ liệu đầu vào
        String name        = ProductName.getText().trim();
        String category    = CategoryChoice.getValue();
        String description = ProductDescription.getText().trim();
        String initPriceStr = InitPrice.getText().trim();
        String iaPriceStr   = IAPrice.getText().trim();
        LocalDate endDate   = EndDate.getValue();
        Integer endHour     = EndHour.getValue();

        if (name.isEmpty()) {
            AlertBox.display("Vui lòng nhập tên sản phẩm.");
            return;
        }
        if (description.isEmpty()) {
            AlertBox.display("Vui lòng nhập mô tả sản phẩm.");
            return;
        }
        if (initPriceStr.isEmpty()) {
            AlertBox.display("Vui lòng nhập giá khởi điểm.");
            return;
        }
        if (endDate == null) {
            AlertBox.display("Vui lòng chọn ngày kết thúc.");
            return;
        }

        BigDecimal initPrice;
        BigDecimal iaPrice;

        try {
            initPrice = new BigDecimal(initPriceStr);
            if (initPrice.compareTo(BigDecimal.ZERO) <= 0) {
                AlertBox.display("Giá khởi điểm phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertBox.display("Giá khởi điểm không hợp lệ.");
            return;
        }

        // Nếu không nhập giá IA, mặc định = 50 × giá khởi điểm
        if (iaPriceStr.isEmpty()) {
            iaPrice = initPrice.multiply(BigDecimal.valueOf(50));
        } else {
            try {
                iaPrice = new BigDecimal(iaPriceStr);
                if (iaPrice.compareTo(initPrice) <= 0) {
                    AlertBox.display("Giá chốt ngay lập tức phải lớn hơn giá khởi điểm.");
                    return;
                }
            } catch (NumberFormatException e) {
                AlertBox.display("Giá chốt ngay lập tức không hợp lệ.");
                return;
            }
        }

        // Ghép ngày + giờ thành LocalDateTime
        LocalDateTime endDateTime = endDate.atTime(endHour, 0);
        if (endDateTime.isBefore(LocalDateTime.now())) {
            AlertBox.display("Thời gian kết thúc phải ở tương lai.");
            return;
        }

        // 2. Tạo request và gọi service
        String userId = ClientSession.getInstance().getUserId();
        ProducPublishRequest request = new ProducPublishRequest(
                userId,
                name,
                category,
                description,
                initPrice,
                iaPrice,
                endDateTime
        );

        ProductPublishResponse response = itemService.publishProduct(request);

        // 3. Xử lý kết quả
        if (response != null && response.success()) {
            AlertBox.display("Đăng sản phẩm thành công!");
            AuctionListSwitch();          // Tự động quay về danh sách
        } else {
            String msg = (response != null && response.message() != null)
                    ? response.message()
                    : "Đăng sản phẩm thất bại. Vui lòng thử lại.";
            AlertBox.display(msg);
        }
    }

    // ────────────────────────────────────────────────────────
    // AuctionListSwitch – quay lại màn hình danh sách đấu giá
    // ────────────────────────────────────────────────────────
    @FXML
    private void AuctionListSwitch() {
        SceneManager.switchTo(SceneName.AUCTION_LIST);
    }
}

