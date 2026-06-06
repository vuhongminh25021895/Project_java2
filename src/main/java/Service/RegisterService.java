package Service;

import Dto.RegisterRequest;
import Dto.RegisterResponse;
import Network.HttpClient;
import Network.ServerConfig;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class RegisterService {
    private static final String BASE_URL = ServerConfig.apiUrl("/api/auth");
    private final OkHttpClient client;
    private final Gson gson;

    public RegisterService() {
        client = HttpClient.getClient();
        gson = new Gson();
    }

    public RegisterRequest registerRequest(String username, String email, String password, String role) {
        return new RegisterRequest(username, email, password, role);
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        String json = gson.toJson(registerRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/register")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!responseBody.isBlank()) {
                return gson.fromJson(responseBody, RegisterResponse.class);
            }
            return new RegisterResponse(response.isSuccessful(), response.isSuccessful() ? "Register success" : "Register failed");
        } catch (IOException e) {
            return new RegisterResponse(false, "Không thể kết nối tới server.");
        }
    }
}
