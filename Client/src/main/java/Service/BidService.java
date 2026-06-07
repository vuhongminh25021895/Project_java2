package Service;

import Config.ApiConfig;
import Dto.Request.BidRequest;
import Dto.Response.BidHistoryResponse;
import Dto.Response.BidResponse;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.net.http.HttpRequest;
import java.util.List;

public class BidService extends BaseApiService{

    public BidResponse getBidDetail(String auctionid) {
        Request request = new Request.Builder()
                .url(ApiConfig.BID + "/detail?id=" + auctionid)
                .build();
        return execute(request, BidResponse.class, new BidResponse(false, null, null, null, null));
    }

    public BidResponse placebid(BidRequest request) {
        String json = gson.toJson(request);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request1 = new Request.Builder()
                .url((ApiConfig.BID + "/bid"))
                .post(body)
                .build();
        return execute(request1, BidResponse.class, null);
    }

    public List<BidHistoryResponse> getMyBidHistory(String userId) {
        Request request = new Request.Builder()
                .url(ApiConfig.BID + "/history?id=" + userId)
                .build();
        return execute(request, new TypeToken<List<BidHistoryResponse>>() {}.getType(), null);
    }
}
