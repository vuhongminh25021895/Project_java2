package Model;
import Model.UserRole;

public class User extends Entity {
    private String userName;
    private String email;
    private UserRole userRole;
    private String password;
    private String phonenumber;

    public User(){}

    public User(String userName, String password, String email, String phonenumber) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
    @Override
    public String printInfo() {
        String info = userRole + " - " + userName + " - " + email;
        return info;
    }

}
