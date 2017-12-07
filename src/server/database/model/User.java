package server.database.model;

/**
 * Models an object from the Users table in the database
 */
public class User {

    private String uid;
    private String personID;
    private String userName;
    private String password;
    private String email;

    /**
     * Creates a User, representing an end-user who can access the service
     *
     * @param uid a unique User identifier
     * @param pid the ID of the Person representing this user
     * @param userName the user's unique userName, used for logging in
     * @param pwd the user's password
     * @param email the user's email address
     */
    public User(String uid, String pid, String userName, String pwd, String email) {
        this.uid = uid;
        this.personID = pid;
        this.userName = userName;
        this.password = pwd;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getPersonID() {
        return personID;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return userName;
    }
}
