package Service;

import Config.ApiConfig;
import Dto.Response.AuctionCardResponse;
import Dto.Response.AuctionDetailResponse;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;

public class AuctionService extends BaseApiService{

    public AuctionService() {}

    public List<AuctionCardResponse> getAllAuctions() {
        Request request = new Request.Builder()
                .url(ApiConfig.AUCTIONS + "/getcards")
                .build();
        return execute(request, new TypeToken<List<AuctionCardResponse>>() {}.getType(), new ArrayList<>());
    }

    public AuctionDetailResponse getDetails(String auctionId) {
        Request request = new Request.Builder()
                .url(ApiConfig.AUCTIONS + "/getdetail?id=" + auctionId)
                .build();
        return execute(request, AuctionDetailResponse.class, new AuctionDetailResponse(false, null, null, null, null, null, null, null, null, null, null));
    }

    public List<AuctionCardResponse> getAllMyAuctions(String userId) {
        Request request = new Request.Builder()
                .url(ApiConfig.AUCTIONS + "/mycards?id=" + userId)
                .build();
        return execute(request, new TypeToken<List<AuctionCardResponse>>() {}.getType(), new ArrayList<>());
    }
}
