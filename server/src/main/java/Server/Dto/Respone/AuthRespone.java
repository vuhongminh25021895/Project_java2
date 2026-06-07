package Server.Dto.Respone;

public record AuthRespone (
        boolean success,
        String message,
        String token,
        String userId,
        String username,
        String role
) {}
