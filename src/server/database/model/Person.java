package server.database.model;

/**
 * Models an object from the Persons table in the database
 */
public class Person {

    private String personID;
    private String descendant;
    private String firstName;
    private String lastName;
    private String gender;
    private String father;
    private String mother;
    private String spouse;

    /**
     * Creates a Person, which represents a person in a user's family tree
     *
     * @param pid a unique Person identifier
     * @param firstName the person's first name
     * @param lastName the person's last name
     * @param gender the person's gender
     * @param fatherID the ID of the person's father
     * @param motherID the ID of the person's mother
     * @param spouseID the ID of the person's spouse
     */
    public Person(String pid, String descendant, String firstName, String lastName, String gender, String fatherID,
                  String motherID, String spouseID) {
        this.personID = pid;
        this.descendant = descendant;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.father = fatherID;
        this.mother = motherID;
        this.spouse = spouseID;
    }

    public Person(String pid, String descendant, String firstName, String lastName, String gender) {
        this.personID = pid;
        this.descendant = descendant;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getDescendant() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
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

    public String getPersonID() {
        return personID;
    }

    public String getFather() {
        return father;
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

    public String getMother() {
        return mother;
    }

    public String getSpouse() {
        return spouse;
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
