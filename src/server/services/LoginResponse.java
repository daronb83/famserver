package server.services;


/**
 * Models a JSON response for the LoginService
 */
public class LoginResponse implements Response {

    private String authToken;
    private String userName;
    private String personID;

    /**
     * Models a JSON response for the LoginService
     *
     * @param authToken a string allowing a user to temporarily authenticate without a
     *                  username/password
     * @param userName the user's username
     * @param personID the id of the Person associated with this user
     */
    public LoginResponse(String authToken, String userName, String personID) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getPersonID() {
        return personID;
    }

}
