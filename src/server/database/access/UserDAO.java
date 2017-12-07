package server.database.access;

import java.sql.*;
import java.util.logging.Level;

import server.database.DatabaseError;
import server.database.model.User;

/**
 * Manages all User related database operations
 */
public class UserDAO extends DAO {

    private static final String createStmt =
                "CREATE TABLE Users (\n" +
                    "\tuid TEXT NOT NULL PRIMARY KEY UNIQUE,\n" +
                    "\tpid TEXT,\n" +
                    "\tusername TEXT NOT NULL UNIQUE,\n" +
                    "\tpwd TEXT NOT NULL,\n" +
                    "\temail TEXT NOT NULL UNIQUE,\n" +
                    "\tFOREIGN KEY(pid) REFERENCES Persons(pid)\n" +
                    ");";

    private static final String dropStmt =
                "DROP TABLE IF EXISTS Users";

    private static final String loginQuery =
                "SELECT uid, pid, email\n" +
                    "FROM Users\n" +
                    "WHERE username = ? AND pwd = ?;";

    private static final String userIdQuery =
                "SELECT pid, username, pwd, email\n" +
                    "FROM Users\n" +
                    "WHERE uid = ?;";

    private static final String usernameQuery =
            "SELECT uid, pid, pwd, email\n" +
                    "FROM Users\n" +
                    "WHERE username = ?;";

    private static final String insert =
                "INSERT INTO Users\n" +
                    "Values (?, ?, ?, ?, ?);";

    /**
     * Manages all database operations for users
     *
     * @param connection
     */
    public UserDAO(Connection connection) {
        super(connection);
    }

    /**
     * Creates the Users table in the database
     *
     * @throws DatabaseError if there is any problem performing the query
     */
    public void createTable() throws DatabaseError {
        createTable(createStmt, dropStmt);
    }

    /**
     * Selects a User from the database by username
     *
     * @param username the User's username
     * @param password the User's password
     * @return the selected User
     * @throws DatabaseError if there is any problem performing the query
     */
    public User login(String username, String password) throws DatabaseError {
        assert username != null : "null username";
        assert password != null : "null password";
        logger.info("Entering UDAO login: Username: " + username + " Password: " + password);

        assert connection != null : "null connection";
        User output;

        try (PreparedStatement stmt = connection.prepareStatement(loginQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            String uid = rs.getString(1);
            String pid = rs.getString(2);
            String email = rs.getString(3);

            output = new User(uid, pid, username, password, email);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseError("Login failed");
        }

        return output;
    }

    /**
     *
     * @param uid
     * @return
     * @throws DatabaseError
     */
    public User getUserById(String uid) throws DatabaseError {
        assert uid != null : "null userID";
        logger.info("Getting user: " + uid);

        assert connection != null : "null connection";

        try (PreparedStatement stmt = connection.prepareStatement(userIdQuery)) {
            stmt.setString(1, uid);
            ResultSet rs = stmt.executeQuery();

            String pid = rs.getString(1);
            String username = rs.getString(2);
            String password = rs.getString(3);
            String email = rs.getString(4);

            logger.info("User " + uid + " retrieved successfully");
            return new User(uid, pid, username, password, email);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseError("getUserById failed");
        }
    }

    public User getUserByUsername(String username) {
        assert username != null : "null username";
        logger.info("Getting user: " + username);

        assert connection != null : "null connection";

        try (PreparedStatement stmt = connection.prepareStatement(usernameQuery)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            String uid = rs.getString(1);
            String pid = rs.getString(2);
            String password = rs.getString(3);
            String email = rs.getString(4);

            logger.info("User " + username + " retrieved successfully");
            return new User(uid, pid, username, password, email);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseError("getUserByUsername failed");
        }
    }

    /**
     * Inserts a given User object into the database
     *
     * @param user the User object to insert
     * @throws DatabaseError if there is any problem performing the query
     */
    public void insert(User user) throws DatabaseError {
        assert connection != null : "null connection";
        assert user != null : "null user";
        logger.info("Inserting user: " + user.getUserName());

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {

            stmt.setString(1, user.getUid());
            stmt.setString(2, user.getPersonID());
            stmt.setString(3, user.getUserName());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getEmail());

            if (stmt.executeUpdate() != 1) {
                throw new DatabaseError("insertUser failed to execute");
            }

            logger.info("User inserted successfully");
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseError("insertUser failed");
        }
    }

}
