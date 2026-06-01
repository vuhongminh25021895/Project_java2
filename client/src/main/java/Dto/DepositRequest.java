package Dto;

public class DepositRequest {
    private String username;
    private Long amount;

    public DepositRequest(String username, Long amount) {
        this.username = username;
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public Long getAmount() {
        return amount;
    }
}
