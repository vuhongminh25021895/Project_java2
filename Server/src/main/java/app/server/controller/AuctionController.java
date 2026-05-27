package app.server.controller;

import app.server.dto.request.AuctionRequest;
import app.server.dto.response.AuctionResponse;
import app.server.service.AuctionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(
            AuctionService auctionService
    ) {
        this.auctionService =
                auctionService;
    }

    @GetMapping
    public List<AuctionResponse> getAll() {

        return auctionService
                .getAllAuctions();
    }

    @PostMapping
    public AuctionResponse create(
            @RequestBody AuctionRequest request
    ) {

        return auctionService
                .createAuction(request);
    }

    @PostMapping("/{auctionId}/cancel")
    public AuctionResponse cancelAuction(
            @PathVariable String auctionId,
            @RequestBody AuctionRequest request
    ) {

        return auctionService.cancelAuction(
                auctionId,
                request.getSellerId()
        );
    }

    @PostMapping("/{auctionId}/join")
    public AuctionResponse joinAuction(
            @PathVariable String auctionId,
            @RequestBody AuctionRequest request
    ) {

        return auctionService.joinAuction(
                auctionId,
                request.getUserId()
        );
    }

    @PostMapping("/{auctionId}/leave")
    public void leaveAuction(
            @PathVariable String auctionId,
            @RequestBody AuctionRequest request
    ) {

        auctionService.leaveAuction(
                auctionId,
                request.getUserId()
        );
    }
}
