package Server.Dto.Respone;

import java.time.LocalDateTime;

public record ProfileRespone(
        Long userId,
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
