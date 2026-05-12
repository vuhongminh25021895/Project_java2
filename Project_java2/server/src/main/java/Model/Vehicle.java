package Model;

public class Vehicle extends Item{
    private int mileage;
    private int vehicleYear;
    private String licensePlate;

    public Vehicle() {}

    public Vehicle(String name, String description, double startingPrice, String imageUrl, Seller seller, int mileage, int vehicleYear, String licensePlate) {
        super(name, description, startingPrice, imageUrl, seller);
        this.mileage = mileage;
        this.vehicleYear = vehicleYear;
        this.licensePlate = licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public void setVehicleYear(int vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    @Override
    public String printInfo() {
        return super.printInfo();
    }
}
