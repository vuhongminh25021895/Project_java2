package WebSocket;

import Dto.Response.BidResponse;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

public class AuctionWebSocketClient {

    private static final AuctionWebSocketClient INSTANCE =
            new AuctionWebSocketClient();

    private final BidNotifier notifier =
            BidNotifier.getInstance();

    private WebSocketStompClient stompClient;

    private boolean connected = false;

    private AuctionWebSocketClient() {
    }

    public static AuctionWebSocketClient getInstance() {
        return INSTANCE;
    }

    public void connect(
            String auctionId
    ) {

        if (connected) {
            return;
        }

        stompClient =
                new WebSocketStompClient(
                        new StandardWebSocketClient()
                );

        stompClient.setMessageConverter(
                new MappingJackson2MessageConverter()
        );

        stompClient.connectAsync(
                "ws://localhost:8080/ws",
                new StompSessionHandlerAdapter() {

                    @Override
                    public void afterConnected(
                            StompSession session,
                            StompHeaders connectedHeaders
                    ) {

                        connected = true;

                        session.subscribe(
                                "/topic/auction/" + auctionId,
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

                                        notifier.notifyObservers(
                                                (BidResponse) payload
                                        );
                                    }
                                }
                        );
                    }

                    @Override
                    public void handleTransportError(
                            StompSession session,
                            Throwable exception
                    ) {

                        exception.printStackTrace();
                    }
                }
        );
    }
}