package Dto;

public class AuctionProductRequest {
    private String sellerUsername;
    private String category;
    private String productName;
    private String description;
    private Integer startPrice;
    private Integer buyNowPrice;
    private String endTime;

    public AuctionProductRequest() {
    }

    public AuctionProductRequest(String sellerUsername, String category, String productName, String description,
                                 Integer startPrice, Integer buyNowPrice, String endTime) {
        this.sellerUsername = sellerUsername;
        this.category = category;
        this.productName = productName;
        this.description = description;
        this.startPrice = startPrice;
        this.buyNowPrice = buyNowPrice;
        this.endTime = endTime;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
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

    public Integer getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Integer startPrice) {
        this.startPrice = startPrice;
    }

    public Integer getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(Integer buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
