package server.services;


/**
 * Models a JSON response for the PersonService
 */
public class PersonResponse implements Response {

    private String descendant;
    private String personID;
    private String firstName;
    private String lastName;
    private String gender;
    private String father;
    private String mother;
    private String spouse;

    /**
     * Models a JSON response for the PersonService
     *
     * @param descendant name of user account this person belongs to
     * @param personID Person's unique ID
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param gender Person's gender
     * @param father ID of the person's father
     * @param mother ID of the person's mother
     * @param spouse ID of the person's spouse
     */
    public PersonResponse(String descendant, String personID, String firstName, String lastName,
                          String gender, String father, String mother, String spouse) {

        this.descendant = descendant;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.father = father;
        this.mother = mother;
        this.spouse = spouse;
    }

    /**
     * Person with null values
     */
    public PersonResponse() {
        descendant = null;
        personID = null;
        firstName = null;
        lastName = null;
        gender = null;
        father = null;
        mother = null;
        spouse = null;

    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }

    public String getDescendant() {
        return descendant;
    }

    public String getPersonID() {
        return personID;
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

    public String getFather() {
        return father;
    }

    public String getMother() {
        return mother;
    }

    public String getSpouse() {
        return spouse;
    }
}
