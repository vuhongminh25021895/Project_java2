package app.client.api;

import java.net.http.HttpClient;

public abstract class BaseApiClient {
    protected final HttpClient httpClient;

    protected final String BASE_URL =
            "http://localhost:8080/api";

    public BaseApiClient() {

        this.httpClient =
                HttpClient.newHttpClient();
    }
}
