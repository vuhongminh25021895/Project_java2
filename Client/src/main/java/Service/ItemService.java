package Service;

import Config.ApiConfig;
import Dto.Request.ProducPublishRequest;
import Dto.Response.MyItemResponse;
import Dto.Response.ProductPublishResponse;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.List;

public class ItemService extends BaseApiService {
    public List<MyItemResponse> getMyItems(String userId) {
        Request request = new Request.Builder()
                .url(ApiConfig.ITEM + "/getitems?id=" + userId )
                .build();
        return execute(request, new TypeToken<List<MyItemResponse>>() {}.getType(), null);
    }

    public ProductPublishResponse publishProduct(ProducPublishRequest publishRequest) {
        String json = gson.toJson(publishRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(ApiConfig.ITEM + "/publish")
                .post(body)
                .build();

        return execute(request, ProductPublishResponse.class,
                new ProductPublishResponse(false, null, null, "Không thể kết nối đến server."));
    }
}
