package Server.Exception;

import Server.Enums.AuctionStatus;

public class AuctionAlreadyClosedException extends AuctionException {
    private final AuctionStatus closedStatus;

    public AuctionAlreadyClosedException(String auctionId, AuctionStatus closedStatus) {
        super("AUCTION_ALREADY_CLOSED",
                String.format("Phiên đấu giá [%s] đã kết thúc (trạng thái: %s).", auctionId, closedStatus));
        this.closedStatus = closedStatus;
    }

    public AuctionStatus getClosedStatus() { return closedStatus; }
}

