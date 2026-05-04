package com.javaproject.signin;

public class User {
    private String username;
    private String password;
    private String email;
    private String sdt;
    private String role;

    public User(String username, String password, String email, String sdt, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.sdt = sdt;
        this.role = role;
    }

}
