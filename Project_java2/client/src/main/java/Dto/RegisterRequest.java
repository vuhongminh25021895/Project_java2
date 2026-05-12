package Dto;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role;

    public RegisterRequest(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
