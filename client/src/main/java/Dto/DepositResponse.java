package Dto;

public class DepositResponse {
    private boolean success;
    private String message;
    private Long newBalance;

    public DepositResponse(boolean success, String message, Long newBalance) {
        this.success = success;
        this.message = message;
        this.newBalance = newBalance;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getNewBalance() {
        return newBalance;
    }
}
