package Dto;

public class BidHistoryPoint {
    private Integer price;
    private String time;

    public BidHistoryPoint() {
    }

    public BidHistoryPoint(Integer price, String time) {
        this.price = price;
        this.time = time;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
