package Service;

import Config.ApiConfig;
import Dto.Request.BidRequest;
import Dto.Respone.BidRespone;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BidService extends BaseApiService{

    public BidRespone getBidDetail(String auctionid) {
        Request request = new Request.Builder()
                .url(ApiConfig.BID + "/detail?=" + auctionid)
                .build();
        return execute(request, BidRespone.class, new BidRespone(null, null, null));
    }

    public BidRespone placebid(BidRequest request) {
        String json = gson.toJson(request);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request1 = new Request.Builder()
                .url((ApiConfig.BID + "/bid"))
                .post(body)
                .build();
        return execute(request1, BidRespone.class, null);
    }
}
