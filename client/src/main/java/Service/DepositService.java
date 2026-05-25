package Service;

import Dto.DepositRequest;
import Dto.DepositResponse;
import Network.HttpClient;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class DepositService {
    private static final String BASE_URL = "http://localhost:8080/api/wallet";
    private final OkHttpClient client;
    private final Gson gson;

    public DepositService() {
        client = HttpClient.getClient();
        gson = new Gson();
    }

    public DepositResponse deposit(DepositRequest depositRequest) {
        String json = gson.toJson(depositRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/deposit")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!responseBody.isBlank()) {
                return gson.fromJson(responseBody, DepositResponse.class);
            }

            if (response.isSuccessful()) {
                return new DepositResponse(true, "Nạp tiền thành công.", null);
            }
            return new DepositResponse(false, "Không thể nạp tiền.", null);
        } catch (IOException e) {
            return new DepositResponse(false, "Không thể kết nối tới server.", null);
        }
    }
}
