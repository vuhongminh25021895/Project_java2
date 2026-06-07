package Server.Dto.Request;

import java.math.BigDecimal;

public record DepositRequest(
        BigDecimal amount
) {
}
