package server.database.access;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.database.DatabaseError;

/**
 * Abstract class for DAOs to inherit from
 */
abstract class DAO {

    protected static Logger logger;

    static {
        logger = Logger.getLogger("famServer");
    }

    Connection connection;

    /**
     * Loads the database connection
     *
     * @param connection an open database connection
     */
    DAO(Connection connection) {
        assert connection != null : "null connection";
        this.connection = connection;
    }

    /**
     * Drops a specified table if it exists and creates a new copy of the table
     *
     * @param createStmt the sql statement for creating the table
     * @param dropStmt the sql statement for dropping the table
     * @throws DatabaseError if there is any issue
     */
    void createTable(String createStmt, String dropStmt) throws DatabaseError {
        assert connection != null : "null connection";

        try (Statement stmt = connection.createStatement()) {
            assert dropStmt != null;
            stmt.executeUpdate(dropStmt);
            assert createStmt != null;
            stmt.executeUpdate(createStmt);
            logger.info("CreateTable succeeded");
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseError("CreateTable failed:" + e.getMessage());
        }
    }
}
