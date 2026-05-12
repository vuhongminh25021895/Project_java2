package Dto;

public class RegisterRespone {
    private boolean success;
    private String message;

    public RegisterRespone(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
