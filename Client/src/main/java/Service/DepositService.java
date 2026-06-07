package Service;

import Config.ApiConfig;

import Dto.Request.DepositRequest;

import Dto.Response.DepositResponse;
import okhttp3.MediaType;

import okhttp3.Request;
import okhttp3.RequestBody;



public class DepositService extends BaseApiService {

    public DepositResponse deposit(DepositRequest depostiRequest) {
        String json = gson.toJson(depostiRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(ApiConfig.WALLET)
                .post(body)
                .build();
        return execute(request, DepositResponse.class, null);
    }
}
