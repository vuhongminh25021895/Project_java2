package Server.Controller;
import Server.Dto.Respone.AuctionCardRespone;
import Server.Dto.Respone.AuctionDetailRespone;
import Server.Service.AuctionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping("/getcards")
    public List<AuctionCardRespone> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

    @GetMapping("/getdetail")
    public AuctionDetailRespone getDetail(@RequestParam  String auctionId) {
        return auctionService.getDetail(auctionId);
    }

}