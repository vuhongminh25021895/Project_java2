package Model;

public class Vehicle extends Item{
    private int mileage;
    private int vehicleYear;
    private String licensePlate;

    protected Vehicle(String name, String description, double startingPrice, String imageUrl, Seller seller, int mileage, int vehicleYear, String licensePlate) {
        super(name, description, startingPrice, imageUrl, seller);
        this.mileage = mileage;
        this.vehicleYear = vehicleYear;
        this.licensePlate = licensePlate;
    }

    @Override
    public String printInfo() {
        return super.printInfo();
    }
}
