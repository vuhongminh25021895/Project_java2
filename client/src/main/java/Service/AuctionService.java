package Service;

import Config.ApiConfig;
import Dto.Respone.AuctionCardRespone;
import Dto.Respone.AuctionDetailRespone;
import Dto.Respone.AuctionRespone;
import Dto.Respone.BidRespone;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;

public class AuctionService extends BaseApiService{

    public AuctionService() {}

    public List<AuctionCardRespone> getAllAuctions() {
        Request request = new Request.Builder()
                .url(ApiConfig.AUCTIONS + "/getcards")
                .build();
        return execute(request, new TypeToken<List<AuctionCardRespone>>() {}.getType(), new ArrayList<>());
    }

    public AuctionDetailRespone getDetails(String auctionId) {
        Request request = new Request.Builder()
                .url(ApiConfig.AUCTIONS + "/getdetail?id=" + auctionId)
                .build();
        return execute(request, AuctionDetailRespone.class, new AuctionDetailRespone(false, null, null, null, null, null, null, null, null, null, null));
    }

    public List<AuctionCardRespone> getAllMyAuctions(String userId) {
        Request request = new Request.Builder()
                .url(ApiConfig.AUCTIONS + "/getcards?id=" + userId)
                .build();
        return execute(request, new TypeToken<List<AuctionCardRespone>>() {}.getType(), new ArrayList<>());
    }
}
