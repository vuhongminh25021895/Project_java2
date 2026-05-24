package app.server.websocket;

import app.shared.dto.request.BidRequest;
import app.shared.dto.response.BidResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import app.server.service.BidService;

@Controller
public class BidSocketController {
    private final BidService bidService;

    private final SimpMessagingTemplate messagingTemplate;

    public BidSocketController(
            BidService bidService,
            SimpMessagingTemplate messagingTemplate) {

        this.bidService = bidService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/bid")
    public void handleBid(BidRequest request) {

        BidResponse response =
                bidService.placeBid(request);

        messagingTemplate.convertAndSend(
                "/topic/auction/" + request.getAuctionId(),
                response
        );
    }
}
