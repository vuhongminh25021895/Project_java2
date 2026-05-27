package app.client.websocket;

import app.client.dto.response.BidResponse;

import org.springframework.messaging.converter
        .MappingJackson2MessageConverter;

import org.springframework.messaging.simp.stomp.*;

import org.springframework.web.socket.client
        .standard.StandardWebSocketClient;

import org.springframework.web.socket.messaging
        .WebSocketStompClient;

import java.lang.reflect.Type;

public class AuctionSocketClient {

    private StompSession session;

    private BidUpdateListener listener;

    public void setListener(
            BidUpdateListener listener
    ) {

        this.listener = listener;
    }

    public void connect() throws Exception {

        WebSocketStompClient stompClient =
                new WebSocketStompClient(
                        new StandardWebSocketClient()
                );

        stompClient.setMessageConverter(
                new MappingJackson2MessageConverter()
        );

        session =
                stompClient.connectAsync(
                        "ws://localhost:8080/ws",
                        new StompSessionHandlerAdapter() {
                        }
                ).get();
    }

    public void subscribeAuction(String auctionId) {

        session.subscribe(

                "/topic/auction/" +
                        auctionId,

                new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(
                            StompHeaders headers
                    ) {
                        return BidResponse.class;
                    }

                    @Override
                    public void handleFrame(
                            StompHeaders headers,
                            Object payload
                    ) {

                        BidResponse response =
                                (BidResponse) payload;

                        if (listener != null) {

                            listener.onBidUpdated(response);
                        }
                    }
                }
        );
    }
}