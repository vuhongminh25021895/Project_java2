package Model;

public class Art extends Item{
    private String artist;
    private int year;
    private Artmedium medium;
    private boolean authenticated;

    protected Art() {}

    protected Art(String name, String description, double startingPrice, String imageUrl, Seller seller, String artist, int year, Artmedium medium, boolean authenticated) {
        super(name, description, startingPrice, imageUrl, seller);
        this.artist = artist;
        this.year = year;
        this.medium = medium;
        this.authenticated = authenticated;
    }

    @Override
    public String printInfo() {
        return super.printInfo();
    }
}
