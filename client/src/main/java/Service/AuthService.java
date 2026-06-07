package Service;

import Config.ApiConfig;
import Dto.Request.LoginRequest;
import Dto.Request.RegisterRequest;
import Dto.Respone.AuthRespone;
import Scene.SceneManager;
import Scene.SceneName;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthService extends BaseApiService {

    public AuthRespone login(LoginRequest loginRequest) {
        String json = gson.toJson(loginRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request =  new Request.Builder()
                .url(ApiConfig.AUTH + "/login")
                .post(body)
                .build();
        return execute(request, AuthRespone.class, new AuthRespone(false, "Connection error", null, null, null, null));
    }

    public AuthRespone signup(RegisterRequest signupRequest) {
        String json = gson.toJson(signupRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(ApiConfig.AUTH + "/signup")
                .post(body)
                .build();
        return execute(request, AuthRespone.class, new AuthRespone(false, "Connection error", null, null, null, null));
    }

    public void loginSuccess() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        pause.setOnFinished(event -> {
                SceneManager.switchTo(SceneName.AUCTION_LIST);
        });

    }
}
