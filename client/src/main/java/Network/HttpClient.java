package Network;

import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    private HttpClient() {
    }
    public static OkHttpClient getClient() {
        return client;
    }

}
