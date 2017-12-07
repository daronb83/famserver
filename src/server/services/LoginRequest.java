package server.services;


/**
 * Models a JSON request for the LoginService
 */
public class LoginRequest implements Request {

    private String userName, password;

    /**
     * Models a JSON request for the LoginService
     *
     * @param userName the user's username
     * @param password the user's password
     */
    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public LoginRequest() {
        userName = null;
        password = null;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
