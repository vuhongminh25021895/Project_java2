package Dto.Response;

public record DepositResponse(
        boolean success,
        Long newBalance
) {
}
