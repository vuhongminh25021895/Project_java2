package Dto;

public class DepositResponse {
    private boolean success;
    private String message;
    private Long newBalance;

    public DepositResponse() {
    }

    public DepositResponse(boolean success, String message, Long newBalance) {
        this.success = success;
        this.message = message;
        this.newBalance = newBalance;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Long newBalance) {
        this.newBalance = newBalance;
    }
}
