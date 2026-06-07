package Network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import Interceptor.JwtInerceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClientFactory {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(new JwtInerceptor())
            .build();

    private HttpClientFactory() {}

    public static OkHttpClient getClient() {
        return client;
    }


}
