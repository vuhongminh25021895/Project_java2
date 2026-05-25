package Dto;

public class DepositRequest {
    private String username;
    private Long amount;

    public DepositRequest() {
    }

    public DepositRequest(String username, Long amount) {
        this.username = username;
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
