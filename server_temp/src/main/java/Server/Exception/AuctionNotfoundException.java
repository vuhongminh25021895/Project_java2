package Server.Exception;

public class AuctionNotfoundException extends AuctionException {
    private final String auctionId;

    public AuctionNotfoundException(String auctionId) {
        super("AUCTION_NOT_FOUND",
                "Không tìm thấy phiên đấu giá với ID: " + auctionId);
        this.auctionId = auctionId;
    }

    public String getAuctionId() { return auctionId; }
}

