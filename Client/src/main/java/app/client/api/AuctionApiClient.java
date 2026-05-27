package app.client.api;

import app.client.dto.response.AuctionResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;

public class AuctionApiClient
        extends BaseApiClient {

    private final ObjectMapper mapper =
            new ObjectMapper();

    public List<AuctionResponse> getAuctions()
            throws Exception {

        HttpRequest request =
                HttpRequest.newBuilder()

                        .uri(
                                new URI(
                                        BASE_URL +
                                                "/auctions"
                                )
                        )

                        .GET()

                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString()
                );

        return mapper.readValue(
                response.body(),
                new TypeReference<>() {}
        );
    }
}
