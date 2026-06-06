package Service;

import Config.ApiConfig;
import Dto.Respone.AuctionCardRespone;
import Dto.Respone.AuctionDetailRespone;
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
        List<AuctionCardRespone> list = execute(request, new TypeToken<List<AuctionCardRespone>>() {}.getType(), new ArrayList<>());
        return list;

    }

    public AuctionDetailRespone getDetails(Long auctionid) {
        Request request = new Request.Builder()
                .url(ApiConfig.AUCTION + "/getdetail" + "/" + auctionid)
                .build();
        return execute(request, AuctionDetailRespone.class, new AuctionDetailRespone(false, null, null, null, null, null, null, null, null, null, null));
    }

}
