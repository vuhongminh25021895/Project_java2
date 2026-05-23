package Service;

import Dto.AuctionProductItem;
import Dto.AuctionProductRequest;
import Dto.AuctionProductResponse;
import Network.HttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class AuctionProductService {
    private static final String BASE_URL = "http://localhost:8080/api/products";
    private final OkHttpClient client;
    private final Gson gson;

    public AuctionProductService() {
        client = HttpClient.getClient();
        gson = new Gson();
    }

    public AuctionProductResponse publish(AuctionProductRequest auctionProductRequest) {
        String json = gson.toJson(auctionProductRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!responseBody.isBlank()) {
                return gson.fromJson(responseBody, AuctionProductResponse.class);
            }
            return new AuctionProductResponse(response.isSuccessful(),
                    response.isSuccessful() ? "Đăng sản phẩm thành công." : "Đăng sản phẩm thất bại.");
        } catch (IOException e) {
            return new AuctionProductResponse(false, "Không thể kết nối tới server.");
        }
    }

    public List<AuctionProductItem> fetchProducts() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return Collections.emptyList();
            }

            String responseBody = response.body().string();
            if (responseBody.isBlank()) {
                return Collections.emptyList();
            }

            Type listType = new TypeToken<List<AuctionProductItem>>() { }.getType();
            List<AuctionProductItem> products = gson.fromJson(responseBody, listType);
            return products == null ? Collections.emptyList() : products;
        }
    }
}
