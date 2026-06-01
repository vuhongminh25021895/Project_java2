package Service;

import Dto.LoginRequest;
import Dto.LoginResponse;
import Network.HttpClient;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class LoginService {
    private static final String BASE_URL = "http://localhost:8080/api/auth";
    private final OkHttpClient client;
    private final Gson gson;

    public LoginService() {
        client = HttpClient.getClient();
        gson = new Gson();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        String json = gson.toJson(loginRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/login")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!responseBody.isBlank()) {
                return gson.fromJson(responseBody, LoginResponse.class);
            }
            return new LoginResponse(response.isSuccessful(), response.isSuccessful() ? "Login success" : "Login failed", null);
        } catch (IOException e) {
            return new LoginResponse(false, "Không thể kết nối tới server.", null);
        }
    }
}
