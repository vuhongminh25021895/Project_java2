package Service;

import Dto.RegisterRequest;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

public class RegisterService {
    private static final String BASE_URL = "http://localhost:8080/api/auth";
    private final OkHttpClient client;
    private final Gson gson;

    public RegisterService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public RegisterRequest registerRequest(String username, String email, String password, String phone) {
        RegisterRequest request = new RegisterRequest(username, email, password, phone);
        return request;
    }

    public boolean register(RegisterRequest registerRequestrequest) throws IOException {
        String json = gson.toJson(registerRequestrequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder().url(BASE_URL).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
