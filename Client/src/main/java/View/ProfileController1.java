package View;

import Dto.Response.BidHistoryResponse;
import Dto.Response.MyItemResponse;
import Dto.Response.ProfileResponse;
import Dto.Response.AuctionCardResponse;
import Scene.SceneManager;
import Scene.SceneName;
import Service.*;
import Session.ClientSession;
import Util.AlertBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

    /**
     * Controller cho màn hình Profile.
     *
     * Chức năng:
     *  1. loadProfile()      – Hiển thị thông tin cá nhân người dùng
     *  2. showBidHistory()   – Lấy & hiển thị lịch sử đặt giá
     *  3. showMyProducts()   – Lấy & hiển thị tất cả sản phẩm của người dùng
     *  4. showMyAuctions()   – Lấy & hiển thị các phiên đấu giá của người dùng
     *  5. goToPublish()      – Chuyển sang màn hình đăng sản phẩm mới
     */
    public class ProfileController1 {

        // ── Thông tin cá nhân ───────────────────────────────────
        @FXML
        private Label fullnameLabel;
        @FXML private Label usernameLabel;
        @FXML private Label emailLabel;
        @FXML private Label memberSinceLabel;
        @FXML private Label participatedLabel;
        @FXML private Label wonLabel;
        @FXML private Label revenueLabel;
        @FXML private Label productsLabel;

        // ── Nút điều hướng ──────────────────────────────────────
        @FXML private Button myAuctionsBtn;
        @FXML private Button bidHistoryBtn;
        @FXML private Button wonAuctionsBtn;
        @FXML private Button myProductsBtn;

        // ── Bảng dữ liệu chung ──────────────────────────────────
        @FXML private Label sectionTitle;
        @FXML private TableView<AuctionRow> dataTable;
        @FXML private TableColumn<AuctionRow, String> col1;
        @FXML private TableColumn<AuctionRow, String> col2;
        @FXML private TableColumn<AuctionRow, String> col3;
        @FXML private TableColumn<AuctionRow, String> col4;

        // ── Services ────────────────────────────────────────────
        private final ProfileService profileService  = new ProfileService();
        private final AuctionService auctionService  = new AuctionService();
        private final BidService bidService      = new BidService();
        private final ItemService itemService = new ItemService();

        // ────────────────────────────────────────────────────────
        // initialize
        // ────────────────────────────────────────────────────────
        @FXML
        public void initialize() {
            // Liên kết cột TableView với các property của AuctionRow
            col1.setCellValueFactory(new PropertyValueFactory<>("productName"));
            col2.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
            col3.setCellValueFactory(new PropertyValueFactory<>("timeremain"));
            col4.setCellValueFactory(new PropertyValueFactory<>("status"));

            loadProfile();
        }

        // ────────────────────────────────────────────────────────
        // 1. Lấy Profile
        // ────────────────────────────────────────────────────────

        /**
         * Gọi ProfileService để lấy thông tin chi tiết người dùng từ server,
         * sau đó điền vào các Label trên UI.
         *
         * Endpoint: GET /api/profile
         * Header:   Authorization: Bearer {jwt}   (tự động gắn qua JwtInterceptor)
         */
        private void loadProfile() {
            String userId = ClientSession.getInstance().getUserId();
            ProfileResponse response = profileService.getprofiledetail(userId);

            if (response != null && response.success()) {
                fullnameLabel.setText(response.fullname());
                usernameLabel.setText(response.username());
                emailLabel.setText(response.email());

                if (response.createAt() != null) {
                    memberSinceLabel.setText(
                            "Thành viên từ: " + TimeService.format(response.createAt())
                    );
                }

                participatedLabel.setText(String.valueOf(response.participatedAuctions()));
                wonLabel.setText(String.valueOf(response.wonAuctions()));
                revenueLabel.setText(String.format("%,.0f VNĐ", response.revenue()));
                productsLabel.setText(String.valueOf(response.productscount()));
            } else {
                AlertBox.display("Lấy thông tin thất bại. Vui lòng thử lại.");
            }
        }

        // ────────────────────────────────────────────────────────
        // 2. Lấy Bid History
        // ────────────────────────────────────────────────────────

        /**
         * Gọi BidService.getMyBidHistory() để lấy toàn bộ lịch sử đặt giá
         * của người dùng hiện tại, rồi hiển thị lên dataTable.
         *
         * Endpoint: GET /api/bids/history?id={userId}
         *
         * Mỗi BidHistoryResponse gồm:
         *   - biddername : tên người đặt giá
         *   - amount     : số tiền đặt
         *   - bidTime    : thời điểm đặt
         *   - status     : WINNING / OUTBID / WON / LOST
         */
        @FXML
        private void showBidHistory() {
            String userId = ClientSession.getInstance().getUserId();
            if (userId == null) {
                AlertBox.display("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
                return;
            }

            sectionTitle.setText("Lịch sử đặt giá");

            // Đặt lại tiêu đề cột cho phù hợp ngữ cảnh
            col1.setText("Sản phẩm");
            col2.setText("Số tiền đặt");
            col3.setText("Thời gian");
            col4.setText("Trạng thái");

            List<BidHistoryResponse> bidList = bidService.getMyBidHistory(userId);
            if (bidList == null) {
                AlertBox.display("Không thể tải lịch sử đặt giá.");
                return;
            }

            List<AuctionRow> rows = new ArrayList<>();
            for (BidHistoryResponse item : bidList) {
                if (!item.success()) {
                    AlertBox.display("Lấy lịch sử đấu giá thất bại.");
                    return;
                }
                String bidTime = TimeService.format(item.bidTime());
                rows.add(new AuctionRow(
                        item.biddername(),
                        item.amount(),
                        bidTime,
                        item.status()
                ));
            }

            dataTable.setItems(FXCollections.observableArrayList(rows));
        }

        // ────────────────────────────────────────────────────────
        // 3. Lấy All My Products
        // ────────────────────────────────────────────────────────

        /**
         * Gọi ProductService.getMyItems() để lấy toàn bộ sản phẩm
         * mà người dùng đã đăng, rồi hiển thị lên dataTable.
         *
         * Endpoint: GET /api/products/getitems?id={userId}
         *
         * Mỗi MyItemResponse gồm:
         *   - productId   : ID sản phẩm
         *   - productName : Tên sản phẩm
         *   - currentPrice: Giá hiện tại
         *   - status      : ACTIVE / ENDED / CANCELLED
         *   - endTime     : Thời điểm kết thúc
         */
        @FXML
        private void showMyProducts() {
            String userId = ClientSession.getInstance().getUserId();
            if (userId == null) {
                AlertBox.display("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
                return;
            }

            sectionTitle.setText("Sản phẩm của tôi");

            col1.setText("Tên sản phẩm");
            col2.setText("Giá hiện tại");
            col3.setText("Còn lại");
            col4.setText("Trạng thái");

            List<MyItemResponse> itemList = itemService.getMyItems(userId);
            if (itemList == null) {
                AlertBox.display("Không thể tải danh sách sản phẩm.");
                return;
            }

            List<AuctionRow> rows = new ArrayList<>();
            for (MyItemResponse item : itemList) {
                if (!item.success()) {
                    AlertBox.display("Lấy danh sách sản phẩm thất bại.");
                    return;
                }
                String timeRemain = TimeService.getRemainingTime(item.endTime());
                rows.add(new AuctionRow(
                        item.productName(),
                        item.currentPrice(),
                        timeRemain,
                        item.status()
                ));
            }

            dataTable.setItems(FXCollections.observableArrayList(rows));
        }

        // ────────────────────────────────────────────────────────
        // 4. Lấy My Auctions (các phiên đấu giá tôi tham gia)
        // ────────────────────────────────────────────────────────

        /**
         * Gọi AuctionService.getAllMyAuctions() để lấy danh sách các phiên
         * đấu giá mà người dùng đang/đã tham gia.
         *
         * Endpoint: GET /api/auctions/getcards?id={userId}
         */
        @FXML
        private void showMyAuctions() {
            String userId = ClientSession.getInstance().getUserId();
            if (userId == null) {
                AlertBox.display("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
                return;
            }

            sectionTitle.setText("Phiên đấu giá của tôi");

            col1.setText("Sản phẩm");
            col2.setText("Giá hiện tại");
            col3.setText("Còn lại");
            col4.setText("Trạng thái");

            List<AuctionCardResponse> auctionList = auctionService.getAllMyAuctions(userId);
            if (auctionList == null) {
                AlertBox.display("Không thể tải danh sách phiên đấu giá.");
                return;
            }

            List<AuctionRow> rows = new ArrayList<>();
            for (AuctionCardResponse auction : auctionList) {
                if (!auction.success()) {
                    AlertBox.display("Lấy thông tin phiên đấu giá thất bại.");
                    return;
                }
                String timeRemain = TimeService.getRemainingTime(auction.endTime());
                rows.add(new AuctionRow(
                        auction.productName(),
                        auction.currentPrice(),
                        timeRemain,
                        auction.status()
                ));
            }

            dataTable.setItems(FXCollections.observableArrayList(rows));
        }

        // ────────────────────────────────────────────────────────
        // 5. Chuyển sang màn hình đăng sản phẩm
        // ────────────────────────────────────────────────────────

        /**
         * Chuyển sang SceneName.PRODUCT_PUBLISHING để người dùng
         * điền thông tin và đăng sản phẩm mới.
         */
        @FXML
        private void goToPublish() {
            SceneManager.switchTo(SceneName.PRODUCT_PUBLISHING);
        }
    }


