package server.database.model;

/**
 * Models an object from the AuthTokens table in the database
 */
public class AuthToken {

    private String value;
    private String uid;

    /**
     * Creates an AuthToken, allowing a user to authenticate without using their password for a
     * limited time.
     *
     * @param uid the ID of the User this token belongs to
     * @param value the token's unique value string
     */
    public AuthToken (String value, String uid) {
        this.uid = uid;
        this.value = value;
    }

    public AuthToken() {
        value = null;
        uid = null;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getValue() {
        return value;
    }

}
