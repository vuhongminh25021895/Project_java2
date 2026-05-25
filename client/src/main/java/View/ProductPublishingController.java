package View;

import Dto.AuctionProductRequest;
import Dto.AuctionProductResponse;
import Service.AuctionProductService;
import StageManager.SceneName;
import StageManager.SwitchSceneControll;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class ProductPublishingController {
    private final AuctionProductService auctionProductService = new AuctionProductService();

    @FXML private Label UserName;
    @FXML private TextField ProductName;
    @FXML private TextArea ProductDescription;
    @FXML private TextField InitPrice;
    @FXML private TextField IAPrice; //IA = instant accept
    @FXML private ChoiceBox<String> CategoryChoice;
    @FXML private DatePicker EndDate;

    @FXML
    public void initialize() {
        String displayName = (AuctionListController.getCurrentUsername() == null || AuctionListController.getCurrentUsername().isBlank()) ? "Guest" : AuctionListController.getCurrentUsername();
        UserName.setText("Người dùng: " + displayName);
        if (CategoryChoice.getValue() == null && !CategoryChoice.getItems().isEmpty()) {
            CategoryChoice.setValue(CategoryChoice.getItems().get(0));
        }
        if (EndDate.getValue() == null) {
            EndDate.setValue(LocalDate.now().plusDays(7));
        }
    }

    @FXML
    public void AuctionListSwitch(ActionEvent event) {
        try {
            SwitchSceneControll.switchScene(SceneName.AUCTION_LIST);
        } catch (Exception e) {
            AlertBox.display("Không thể mở trang");
        }
    }

    @FXML
    public void SendAuction(ActionEvent event) {
        String productName = ProductName.getText().trim();
        String description = ProductDescription.getText().trim();
        String category = CategoryChoice.getValue();
        String initPriceStr = InitPrice.getText().trim();
        String iaPriceStr = IAPrice.getText().trim();
        LocalDate endDate = EndDate.getValue();

        if (productName.isEmpty() || initPriceStr.isEmpty() || category == null || category.isBlank()) {
            AlertBox.display("Không thể để trống tên sản phẩm, loại sản phẩm hoặc giá khởi điểm.");
            return;
        }

        if (endDate == null) {
            endDate = LocalDate.now().plusDays(7);
            EndDate.setValue(endDate);
        }

        if (!isInt(initPriceStr)) {
            AlertBox.display("Giá khởi điểm phải là số hợp lệ");
            return;
        }
        int initPrice = Integer.parseInt(initPriceStr);
        if (initPrice <= 0) {
            AlertBox.display("Giá khởi điểm phải lớn hơn 0");
            return;
        }

        int finalPrice;

        if (iaPriceStr.isEmpty()) {
            finalPrice = initPrice * 50;
            IAPrice.setText(String.valueOf(finalPrice));
        } else {
            if (!isInt(iaPriceStr)) {
                AlertBox.display("Giá chốt ngay lập tức phải là số hợp lệ");
                return;
            }
            finalPrice = Integer.parseInt(iaPriceStr);
        }

        if (finalPrice < initPrice) {
            AlertBox.display("Giá chốt ngay phải lớn hơn hoặc bằng giá khởi điểm.");
            return;
        }

        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        if (endDateTime.isBefore(LocalDateTime.now())) {
            AlertBox.display("Ngày kết thúc phải ở hiện tại hoặc tương lai.");
            return;
        }

        String sellerUsername = AuctionListController.getCurrentUsername();
        if (sellerUsername == null || sellerUsername.isBlank()) {
            sellerUsername = "Guest";
        }

        AuctionProductRequest request = new AuctionProductRequest(
                sellerUsername,
                category,
                productName,
                description,
                initPrice,
                finalPrice,
                endDateTime.toString()
        );

        AuctionProductResponse response = auctionProductService.publish(request);
        if (!response.isSuccess()) {
            AlertBox.display(response.getMessage());
            return;
        }

        clearForm();
        AlertBox.display(response.getMessage());

        try {
            SwitchSceneControll.switchScene(SceneName.AUCTION_LIST);
        } catch (Exception e) {
            AlertBox.display("Đăng sản phẩm thành công nhưng không thể mở danh sách đấu giá.");
        }
    }

    private boolean isInt(String str){
        try {
            Integer.parseInt(str);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void clearForm() {
        ProductName.clear();
        ProductDescription.clear();
        InitPrice.clear();
        IAPrice.clear();
        if (!CategoryChoice.getItems().isEmpty()) {
            CategoryChoice.setValue(CategoryChoice.getItems().get(0));
        }
        EndDate.setValue(LocalDate.now().plusDays(7));
    }
}
