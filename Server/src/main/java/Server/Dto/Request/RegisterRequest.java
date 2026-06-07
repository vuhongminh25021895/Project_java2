package Server.Dto.Request;

public record RegisterRequest(
        String fullname,
        String username,
        String email,
        String userRole,
        String password
) {}
