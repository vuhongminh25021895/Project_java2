package app.server.model;

import app.shared.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends BaseEntity {
    private String fullName;
    private String userName;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private String password;

    public User() {}

    protected User(String userName, String password, String email, String fullName) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}
