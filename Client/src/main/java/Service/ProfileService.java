package Service;

import Config.ApiConfig;
import Dto.Respone.ProfileRespone;
import okhttp3.Request;

public class ProfileService extends BaseApiService {

    public ProfileRespone getprofiledetail(String userid) {
        Request request = new Request.Builder()
                .url(ApiConfig.PROFILE)
                .build();
        return execute(request, ProfileRespone.class, null);
    }
}
