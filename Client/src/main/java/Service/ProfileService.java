package Service;

import Config.ApiConfig;
import Dto.Response.ProfileResponse;
import okhttp3.Request;

public class ProfileService extends BaseApiService {

    public ProfileResponse getprofiledetail(String userid) {
        Request request = new Request.Builder()
                .url(ApiConfig.PROFILE)
                .build();
        return execute(request, ProfileResponse.class, null);
    }
}
