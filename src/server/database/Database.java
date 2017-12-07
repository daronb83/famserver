package server.database;

import server.database.access.PersonDAO;
import server.database.access.UserDAO;
import server.database.access.EventDAO;
import server.database.access.AuthTokenDAO;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Manages database connections
 */
public class Database {

    private static Logger logger;

    static {
        logger = Logger.getLogger("famServer");
    }

    private AuthTokenDAO authTokenDAO;
    private EventDAO eventDAO;
    private PersonDAO personDAO;
    private UserDAO userDAO;

    private Connection connection;

    /**
     * Opens a connection to the database
     * @throws DatabaseError if there is a problem opening the database connection
     */
    public void openConnection() throws DatabaseError {
        logger.info("Opening database connection");

        try {
            final String driver = "org.sqlite.JDBC";
            Class.forName(driver);
        }
        catch(ClassNotFoundException e) {
            Error error = new DatabaseError("Could not load database driver");
            logger.log(Level.SEVERE, "Could not load database driver", error);
            throw error;
        }

        final String connectionURL = "jdbc:sqlite:db/server.sqlite";

        try {
            // Open a database connection
            connection = DriverManager.getConnection(connectionURL);
            // Start a transaction
            connection.setAutoCommit(false);
        }
        catch (SQLException e) {
            throw new DatabaseError("Could not connect to database: " + e.getMessage());
        }
    }

    /**
     * Closes a connection to the database
     * @param commit true to commit changes, false to roll back
     * @throws DatabaseError if there is a problem closing the database connection
     */
    public void closeConnection(boolean commit) throws DatabaseError {
        assert connection != null : "No open connection";

        try {
            if (commit) {
                connection.commit();
            }
            else {
                connection.rollback();
            }
        }
        catch (SQLException e) {
            throw new DatabaseError("Failed to commit/rollback: " + e.getMessage());
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException e) {
                throw new DatabaseError("Failed to close connection: " + e.getMessage());
            }
        }

        connection = null;
    }

    /**
     * Checks if a connection to the database is open
     *
     * @return true if open, false if closed
     */
    public boolean connectionOpen() {
        return (connection != null);
    }

    public AuthTokenDAO getAuthTokenDAO() {
        assert connection != null : "You must open a connection before accessing a DAO";
        if (authTokenDAO == null) {
            authTokenDAO = new AuthTokenDAO(connection);
        }
        return authTokenDAO;
    }

    public EventDAO getEventDAO() {
        assert connection != null : "You must open a connection before accessing a DAO";
        if (eventDAO == null) {
            eventDAO = new EventDAO(connection);
        }
        return eventDAO;
    }

    public PersonDAO getPersonDAO() {
        assert connection != null : "You must open a connection before accessing a DAO";
        if (personDAO == null) {
            personDAO = new PersonDAO(connection);
        }
        return personDAO;
    }

    public UserDAO getUserDAO() {
        assert connection != null : "You must open a connection before accessing a DAO";
        if (userDAO == null) {
            userDAO = new UserDAO(connection);
        }
        return userDAO;
    }

}
