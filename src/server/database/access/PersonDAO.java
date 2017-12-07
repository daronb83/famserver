package server.database.access;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import server.database.DatabaseError;
import server.database.model.Person;
import server.database.model.User;


/**
 * Manages all Person related database operations
 */
public class PersonDAO extends DAO {

    private static final String createTable =
                "CREATE TABLE Persons (\n" +
                    "\tpid TEXT NOT NULL PRIMARY KEY UNIQUE,\n" +
                    "\tdescendant TEXT,\n" +
                    "\tname_first TEXT NOT NULL,\n" +
                    "\tname_last TEXT NOT NULL,\n" +
                    "\tgender TEXT NOT NULL,\n" +
                    "\tfather_id TEXT,\n" +
                    "\tmother_id TEXT,\n" +
                    "\tspouse_id TEXT,\n" +
                    "\tFOREIGN KEY(descendant) REFERENCES Users(username),\n" +
                    "\tCHECK (gender IN (\"m\", \"f\"))\n" +
                    ");";

    private static final String dropTable =
                "DROP TABLE IF EXISTS Persons";

    private static final String personByIdQuery =
                "SELECT *\n" +
                    "\tFROM Persons\n" +
                    "\tWHERE pid = ?;";

    private static final String personsByDescendantQuery =
                "SELECT *\n" +
                    "\tFROM Persons\n" +
                    "\tWHERE descendant = ?;";

    private static final String insert =
                "INSERT INTO Persons\n" +
                    "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String delete =
                "DELETE FROM Persons\n" +
                    "\tWHERE descendant = ? AND pid != ?;";

    private static final String update =
                "UPDATE Persons\n" +
                    "\tSET father_id = ?, mother_id = ?, spouse_id = ?\n" +
                    "\tWHERE pid = ?;";


    /**
     * Manages all database operations for the Persons table
     *
     * @param connection an open database connection
     */
    public PersonDAO(Connection connection) {
        super(connection);
    }

    /**
     * Creates the Persons table in the database
     *
     * @throws DatabaseError if there is any problem performing the query
     */
    public void createTable() throws DatabaseError {
        createTable(createTable, dropTable);
    }

    /**
     * Selects a Person by id
     *
     * @param pid the Person's id
     * @return the selected Person
     * @throws DatabaseError if there is any problem performing the query
     */
    public Person getPerson(String pid) throws DatabaseError {
        assert pid != null : "null pid";
        logger.info("Getting person with ID: " + pid);

        assert connection != null;

        try (PreparedStatement stmt = connection.prepareStatement(personByIdQuery)) {
            stmt.setString(1, pid);
            ResultSet rs = stmt.executeQuery();

            String personID = rs.getString(1);
            String descendant = rs.getString(2);
            String firstName = rs.getString(3);
            String lastName = rs.getString(4);
            String gender = rs.getString(5);
            String fatherID = rs.getString(6);
            String motherID = rs.getString(7);
            String spouseID = rs.getString(8);

            logger.info("Person retrieved successfully");
            return new Person(personID, descendant, firstName, lastName, gender, fatherID, motherID,
                              spouseID);
        }
        catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatabaseError("getPerson failed");
        }
    }

    /**
     * Selects all persons associated with a particular username
     *
     * @param descendant the username
     * @return the persons associated with the username
     * @throws DatabaseError if there is any problem performing the query
     */
    public List<Person> getPersonsByUsername(String descendant) throws DatabaseError {
        assert descendant != null : "null descendant";
        logger.info("Getting persons for: " + descendant);

        assert connection != null;

        try (PreparedStatement stmt = connection.prepareStatement(personsByDescendantQuery)) {
            stmt.setString(1, descendant);
            ResultSet rs = stmt.executeQuery();

            List<Person> persons = new ArrayList<>();

            while (rs.next()){
                String personID = rs.getString(1);
                String username = rs.getString(2);
                String firstName = rs.getString(3);
                String lastName = rs.getString(4);
                String gender = rs.getString(5);
                String fatherID = rs.getString(6);
                String motherID = rs.getString(7);
                String spouseID = rs.getString(8);

                persons.add(new Person(personID, username, firstName, lastName, gender, fatherID, motherID,
                        spouseID));
            }

            logger.info("Persons retrieved successfully");
            return persons;
        }
        catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatabaseError("getPersonsByUsername failed");
        }
    }

    /**
     * Inserts a given Person object into the database
     *
     * @param person the Person object to insert
     * @throws DatabaseError if there is any problem performing the query
     */
    public void insert(Person person) throws DatabaseError {
        assert person != null : "null person";
        logger.info("Inserting person: " + person.getPersonID());

        assert connection != null : "null connection";

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getDescendant());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFather());
            stmt.setString(7, person.getMother());
            stmt.setString(8, person.getSpouse());

            if (stmt.executeUpdate() != 1) {
                throw new DatabaseError("insert failed to execute");
            }

            logger.info("Person " + person.getPersonID() + " inserted successfully");
        }
        catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatabaseError("insert failed");
        }
    }

    /**
     * Deletes all people from the database for a given user
     *
     * @param user the user who's data needs to be cleared
     * @throws DatabaseError if there is any problem performing the query
     */
    public void delete(User user) throws DatabaseError {
        assert connection != null : "null connection";
        assert user != null : "null descendant";
        logger.info("Deleting all Persons for: " + user.getUserName());

        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPersonID());
            stmt.execute();

            logger.info("Persons for " + user.getUserName() + " deleted successfully");
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatabaseError("delete failed");
        }
    }

    /**
     * Updates the ids of a Person
     *
     * @param person = the person to update (with correct ids stored)
     */
    public void updateIDs(Person person) {
        assert connection != null : "null connection";
        assert person != null;
        logger.info("Updating IDs " + person.getFirstName() + " " + person.getLastName());

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, person.getFather());
            stmt.setString(2, person.getMother());
            stmt.setString(3, person.getSpouse());
            stmt.setString(4, person.getPersonID());

            if (stmt.executeUpdate() != 1) {
                SQLWarning e = stmt.getWarnings();
                logger.log(Level.WARNING, e.getMessage(), e);
                throw new DatabaseError("updateIds failed to execute");
            }

            logger.info("IDs for " + person.getFirstName() + " " + person.getLastName() +
                        " updated successfully");

        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatabaseError("update failed");
        }
    }
}
