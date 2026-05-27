package app.server.service;

import app.server.model.Auction;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AntiSnipingService {
    private static final long LAST_SECONDS_THRESHOLD = 10;

    private static final long EXTEND_SECONDS = 60;

    public void extendIfNeeded(Auction auction) {

        long secondsLeft = Duration.between(
                LocalDateTime.now(),
                auction.getEndTime()
        ).toSeconds();

        if (secondsLeft <= LAST_SECONDS_THRESHOLD) {
            auction.extendAuctionSeconds(EXTEND_SECONDS);
        }
    }
}
