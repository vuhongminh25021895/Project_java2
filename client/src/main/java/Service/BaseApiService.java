package Service;

import Config.GsonConfig;
import Network.HttpClientFactory;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Type;

public class BaseApiService {
    protected OkHttpClient client = HttpClientFactory.getClient();
    protected Gson gson = GsonConfig.getGson();

    protected <T> T execute(Request request, Type type, T fallback) {
        try(
                Response respone = client.newCall(request).execute()
        ){
            if (!respone.isSuccessful()) {
                return fallback;
            }

            String json = respone.body().string();
            return gson.fromJson(json, type);

        } catch (Exception e){
            e.printStackTrace();;

            return fallback;
        }
    }
}
