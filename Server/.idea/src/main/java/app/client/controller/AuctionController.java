package app.client.controller;

import app.server.model.Auction;
import app.client.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {
    @Autowired
    private AuctionService auctionService;

    @GetMapping
    public List<Auction> getAll() {
        return auctionService.getAllAuctions();
    }

    @PostMapping
    public Auction create(@RequestBody Auction auction) {
        return auctionService.createAuction(auction);
    }
}
