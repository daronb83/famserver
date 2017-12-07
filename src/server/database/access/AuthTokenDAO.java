package server.database.access;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import server.database.DatabaseError;
import server.database.model.AuthToken;

/**
 * Manages all AuthToken related database operations
 */
public class AuthTokenDAO extends DAO {

    private static final String createStmt =
                "CREATE TABLE AuthTokens (\n" +
                    "\tvalue TEXT NOT NULL PRIMARY KEY UNIQUE,\n" +
                    "\tuid TEXT NOT NULL,\n" +
                    "\tFOREIGN KEY(uid) REFERENCES Users(uid)\n" +
                    ");\n";

    private static final String dropStmt =
                "DROP TABLE IF EXISTS AuthTokens";

    private static final String tokenByValueQuery =
                "SELECT uid\n" +
                    "FROM AuthTokens\n" +
                    "WHERE value = ?;";

    private static final String tokensByUserQuery =
                "Select value\n" +
                    "FROM AuthTokens\n" +
                    "WHERE uid = ?;";

    private static final String insert =
                "INSERT INTO AuthTokens\n" +
                    "VALUES (?, ?)";

    /**
     * Manages all AuthToken database operations
     *
     * @param connection an open database connection
     */
    public AuthTokenDAO(Connection connection) {
        super(connection);
    }

    /**
     * Creates the AuthToken table in the database, if needed
     *
     * @throws DatabaseError if there is any problem performing the query
     */
    public void createTable() throws DatabaseError {
        super.createTable(createStmt, dropStmt);
    }

    /**
     * Selects an active AuthToken from the database by token value
     *
     * @param tokenValue the AuthToken's value
     * @return the selected AuthToken
     * @throws DatabaseError if there is any problem performing the query
     */
    public AuthToken getToken(String tokenValue) throws DatabaseError {
        assert connection != null : "null connection";
        AuthToken output;

        try (PreparedStatement stmt = connection.prepareStatement(tokenByValueQuery)) {
            assert tokenValue != null : "null token value";
            stmt.setString(1, tokenValue);

            ResultSet rs = stmt.executeQuery();
            String uid = rs.getString(1);

            output = new AuthToken(tokenValue, uid);
        }
        catch (SQLException e) {
            throw new DatabaseError("Authtoken select failed: " + e.getMessage());
        }

        return output;
    }

    /**
     * Selects all active AuthTokens from the database associated with a given user id
     *
     * @param uid the User's id
     * @return a list of active AuthTokens associated with the user
     * @throws DatabaseError if there is any problem performing the query
     */
    public List<AuthToken> getTokensByUser(String uid) throws DatabaseError {
        ArrayList<AuthToken> output;
        assert connection != null : "null connection";

        try (PreparedStatement stmt = connection.prepareStatement(tokensByUserQuery)) {

            assert uid != null : "null uid";
            stmt.setString(1, uid);
            ResultSet rs = stmt.executeQuery();

            output = new ArrayList<>();

            while (rs.next()) {
                String value = rs.getString(1);
                AuthToken token = new AuthToken(value, uid);
                output.add(token);
            }
        }
        catch (SQLException e) {
            throw new DatabaseError("Authtoken select failed: " + e.getMessage());
        }

        return output;
    }

    /**
     * Inserts an AuthToken object into the database
     *
     * @param token the AuthToken object to insert
     * @throws DatabaseError if there is any problem performing the query
     */
    public void insertToken(AuthToken token) throws DatabaseError {
        assert connection != null : "null connection";

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            assert token != null : "null token";
            stmt.setString(1, token.getValue());
            stmt.setString(2, token.getUid());

            if (stmt.executeUpdate() != 1) {
                throw new DatabaseError("insertToken failed to execute");
            }
        }
        catch (SQLException e) {
            throw new DatabaseError("insertToken failed:" + e.getMessage());
        }
    }
}
