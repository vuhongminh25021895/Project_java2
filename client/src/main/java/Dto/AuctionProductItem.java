package Dto;

public class AuctionProductItem {
    private int id;
    private String category;
    private String productName;
    private String description;
    private int currentPrice;
    private int buyNowPrice;
    private String endTime;
    private String status;
    private String sellerUsername;

    public AuctionProductItem() {
    }

    public AuctionProductItem(int id, String category, String productName, String description, int currentPrice,
                              int buyNowPrice, String endTime, String status, String sellerUsername) {
        this.id = id;
        this.category = category;
        this.productName = productName;
        this.description = description;
        this.currentPrice = currentPrice;
        this.buyNowPrice = buyNowPrice;
        this.endTime = endTime;
        this.status = status;
        this.sellerUsername = sellerUsername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(int buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }
}
