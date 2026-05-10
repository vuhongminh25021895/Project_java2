package Dto;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String phone;

    public RegisterRequest(String username, String email, String password, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}
