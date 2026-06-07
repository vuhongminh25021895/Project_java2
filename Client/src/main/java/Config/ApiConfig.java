package Config;

public final class ApiConfig {

    private static final String DEFAULT_SERVER_URL = "http://100.117.129.110:8080";
    private static final String SERVER_URL_PROPERTY = "auction.server.url";
    private static final String SERVER_URL_ENV = "AUCTION_SERVER_URL";

    public static final String SERVER = resolveServerUrl();

    public static final String API = SERVER + "/api";

    public static final String AUTH = API + "/auth";
    public static final String AUCTIONS = API + "/auctions";
    public static final String BID = API + "/bids";
    public static final String WALLET = API + "/wallets";
    public static final String ITEM = API + "/products";
    public static final String AUCTION = API + "/auction";
    public static final String PROFILE = API + "/profile";

    public static final String WS_SERVER =
            SERVER.replace("http://", "ws://");

    public static final String AUCTION_WS = WS_SERVER + "/ws/auction";

    private ApiConfig() {}

    private static String resolveServerUrl() {
        String propertyValue = System.getProperty(SERVER_URL_PROPERTY);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return removeTrailingSlash(propertyValue.trim());
        }

        String envValue = System.getenv(SERVER_URL_ENV);
        if (envValue != null && !envValue.isBlank()) {
            return removeTrailingSlash(envValue.trim());
        }

        return DEFAULT_SERVER_URL;
    }

    private static String removeTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}