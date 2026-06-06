package Interceptor;

import Session.ClientSession;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class JwtInerceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        String token = ClientSession.getInstance().getJwtToken();

        if (token == null) {
            return chain.proceed(original);
        }

        Request authenticated = original.newBuilder()
                .header("Authorization", "Bearer" + token)
                .build();
        return chain.proceed(authenticated);
    }
}
