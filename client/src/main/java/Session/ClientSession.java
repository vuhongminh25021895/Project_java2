package Session;

public class ClientSession {
    private static final ClientSession Instance = new ClientSession();

    private String jwtToken;
    private String userId;
    private String username;
    private String role;

    private ClientSession() {}

    public ClientSession getClientSession() {
        return Instance;
    }
    public static ClientSession getInstance() {
        return Instance;
    }

    public void login(String jwtToken, String userId, String username, String role) {
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public void logout() {
        this.jwtToken = null;
        this.userId = null;
        this.username = null;
        this.role = null;
    }

    public void setjwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }
}
