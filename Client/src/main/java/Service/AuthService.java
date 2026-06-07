package Service;

import Config.ApiConfig;
import Dto.Request.LoginRequest;
import Dto.Request.RegisterRequest;

import Dto.Response.AuthResponse;
import Scene.SceneManager;
import Scene.SceneName;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthService extends BaseApiService {

    public AuthResponse login(LoginRequest loginRequest) {
        String json = gson.toJson(loginRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request =  new Request.Builder()
                .url(ApiConfig.AUTH + "/login")
                .post(body)
                .build();
        return execute(request, AuthResponse.class, new AuthResponse(false, "Connection error", null, null, null, null));
    }

    public AuthResponse signup(RegisterRequest signupRequest) {
        String json = gson.toJson(signupRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(ApiConfig.AUTH + "/signup")
                .post(body)
                .build();
        return execute(request, AuthResponse.class, new AuthResponse(false, "Connection error", null, null, null, null));
    }

    public void loginSuccess() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        pause.setOnFinished(event -> {
                SceneManager.switchTo(SceneName.AUCTION_LIST);
        });

    }
}
