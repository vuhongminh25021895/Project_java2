package Scene;

public enum SceneName {
    // Xác thực
    LOGIN,
    REGISTER,

    // Đấu giá
    AUCTION_LIST,
    AUCTION_DETAIL,       // Xem chi tiết phiên trước khi tham gia
    BIDDING,
    BID_HISTORY,          // Lịch sử đặt giá của user trong 1 phiên

    // Đăng bán
    PRODUCT_PUBLISHING,
    MY_AUCTIONS,          // Quản lý phiên đấu giá của bản thân

    // Tài khoản & tài chính
    PROFILE,
    DEPOSIT,
    TRANSACTION_HISTORY   // Lịch sử nạp tiền / thanh toán
}