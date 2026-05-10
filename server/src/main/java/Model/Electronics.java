package Model;

public class Electronics extends Item{
    private String brand;
    private int wattage;
    private String warranty;

    public Electronics() {}

    public Electronics(String name, String description, double startingPrice, String imageUrl, Seller seller, String brand, int wattage,  String warranty) {
        super(name, description, startingPrice, imageUrl, seller);
        this.brand = brand;
        this.warranty = warranty;
        this.wattage = wattage;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public void setWattage(int wattage) {
        this.wattage = wattage;
    }

    @Override
    public String printInfo() {
        return "";
    }
}
