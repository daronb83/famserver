package server.services;


/**
 * Models a JSON request for the RegisterService
 */
public class RegisterRequest implements Request {

    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;

    /**
     * Models a JSON request for the RegisterService
     *
     * @param userName the user's username
     * @param password the user's password
     * @param email the user's email address
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param gender the user's gender, either "m" or "f"
     */
    public RegisterRequest(String userName, String password, String email, String firstName,
                           String lastName, String gender) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public RegisterRequest(){

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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }
}
