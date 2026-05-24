package app.server.controller;

import app.shared.dto.request.BidRequest;
import app.shared.dto.response.BidResponse;
import org.springframework.web.bind.annotation.*;
import app.server.service.BidService;

@RestController
@RequestMapping("/api/bids")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public BidResponse placeBid(
            @RequestBody BidRequest request
    ) {

        return bidService.placeBid(request);
    }
}
