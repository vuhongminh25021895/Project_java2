package app.shared.dto.response;

public class AuthResponse {
    private boolean success;
    private String message;
    private String userId;

    public AuthResponse(
            boolean success,
            String message,
            String userId
    ) {
        this.success = success;
        this.message = message;
        this.userId = userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }
}
