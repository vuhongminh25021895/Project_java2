package Network;
public final class ServerConfig {
    private static final String DEFAULT_SERVER_URL = "http://100.117.129.110:8080";
    private static final String SERVER_URL_PROPERTY = "auction.server.url";
    private static final String SERVER_URL_ENV = "AUCTION_SERVER_URL";

    private static final String SERVER_URL = resolveServerUrl();

    private ServerConfig() {
    }

    public static String apiUrl(String path) {
        if (path == null || path.isBlank()) {
            return SERVER_URL;
        }

        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return SERVER_URL + normalizedPath;
    }

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
