package Scene;

// ✅ SceneName.java
public enum SceneName {
    LOGIN("login"),
    REGISTER("register"),
    AUCTION_LIST("auctionlist"),
    AUCTION_CARD("auctioncard"),
    AUCTION_DETAIL("auctiondetail"),
    BIDDING("bidding"),
    DEPOSIT("deposit"),
    MY_AUCTIONS("myauctions"),
    PRODUCT_PUBLISHING("productpublishing"),
    PROFILE("profile");

    private final String fxmlName;
    SceneName(String fxmlName) { this.fxmlName = fxmlName; }
    public String getFxmlName() { return fxmlName; }
}