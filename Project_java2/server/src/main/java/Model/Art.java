package Model;

public class Art extends Item{
    private String artist;
    private int year;
    private Artmedium medium;
    private boolean authenticated;

    public Art() {}

    public Art(String name, String description, double startingPrice, String imageUrl, Seller seller, String artist, int year, Artmedium medium, boolean authenticated) {
        super(name, description, startingPrice, imageUrl, seller);
        this.artist = artist;
        this.year = year;
        this.medium = medium;
        this.authenticated = authenticated;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setMedium(Artmedium artmedium) {
        this.medium = artmedium;
    }

    @Override
    public String printInfo() {
        return super.printInfo();
    }
}
