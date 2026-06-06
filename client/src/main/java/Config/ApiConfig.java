package Config;

public class ApiConfig {
    private ApiConfig() {}

    //Server
    public static final String HOST = "localhost";
    public static final int PORT = 8080;

    //HTTP
    public static final String SERVER = "http://" + HOST + ":" + PORT;

    //REST API
    public static final String API = SERVER + "/api";

    public static final String AUTH = API + "/auth";

    public static final String AUCTIONS = API + "/auctions";

    public static final String BID = API + "/bids";

    public static final String WALLET = API + "/wallets";

    public static final String ITEM = API + "/products";

    public static final String AUCTION = API + "/auction";

    public static final String PROFILE = API + "/profile";
    //Websocket
    public static final String WS_SERVER = "ws://" + HOST + ":" + PORT;
    public static final String AUCTION_WS = WS_SERVER + "/ws/auction";
}
