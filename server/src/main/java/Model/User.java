package Model;
import Model.UserRole;

public class User extends Entity {
    private String fullName;
    private String userName;
    private String email;
    private UserRole userRole;
    private String password;

    protected User(){}

    protected User(String userName, String password, String email, String fullName) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

    @Override
    public String printInfo() {
        String info = userRole + " - " + userName + " - " + email;
        return info;
    }

}
