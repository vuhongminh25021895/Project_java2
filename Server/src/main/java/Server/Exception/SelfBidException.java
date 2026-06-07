package Server.Exception;

public class SelfBidException extends AuctionException {
    public SelfBidException(String sellerId) {
        super("SELF_BID",
                "Người bán không được tự đặt giá sản phẩm của mình (sellerId=" + sellerId + ").");
    }
}
