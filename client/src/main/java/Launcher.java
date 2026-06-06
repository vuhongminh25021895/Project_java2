import java.io.File;

public class Launcher {
    public static void main(String[] args) {
        File cacheDir = new File(System.getProperty("java.io.tmpdir"), "AuctionListApp-javafx-cache");
        cacheDir.mkdirs();
        System.setProperty("javafx.cachedir", cacheDir.getAbsolutePath());
        AuctionApp.main(args);
    }
}
