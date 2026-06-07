package Dto.Response;

import java.time.LocalDateTime;

public record ProfileResponse(
        boolean success,
        String userId,
        String fullname,
        String username,
        String email,
        LocalDateTime createAt,
        Integer participatedAuctions,
        Integer wonAuctions,
        Double revenue,
        Integer productscount
) {
}
