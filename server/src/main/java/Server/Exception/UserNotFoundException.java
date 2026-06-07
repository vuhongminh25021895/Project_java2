package Server.Exception;

public class UserNotFoundException extends AuctionException{
    private final String userId;

    public UserNotFoundException(String userId) {
        super("AUCTION_NOT_FOUND",
                "Không tìm thấy phiên đấu giá với ID: " + userId);
        this.userId = userId;
    }
}
