package app.client.api;

import app.client.dto.request.LoginRequest;
import app.client.dto.response.AuthResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApiClient
        extends BaseApiClient {

    private final ObjectMapper mapper =
            new ObjectMapper();

    public AuthResponse login(
            LoginRequest loginRequest
    ) throws Exception {

        String body =
                mapper.writeValueAsString(
                        loginRequest
                );

        HttpRequest request =
                HttpRequest.newBuilder()

                        .uri(
                                new URI(
                                        BASE_URL +
                                                "/auth/login"
                                )
                        )

                        .header(
                                "Content-Type",
                                "application/json"
                        )

                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(body)
                        )

                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString()
                );

        return mapper.readValue(
                response.body(),
                AuthResponse.class
        );
    }
}
