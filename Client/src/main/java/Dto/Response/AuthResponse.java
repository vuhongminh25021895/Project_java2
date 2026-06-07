package Dto.Response;

public record AuthResponse(
        boolean success,
        String message,
        String token,
        String userId,
        String username,
        String role
) {
}
