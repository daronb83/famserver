package server.database.access;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import server.database.Database;
import server.database.DatabaseError;
import server.database.model.Event;

/**
 * Manages all Event related database operations
 */
public class EventDAO extends DAO {

    private Database db;

    private static final String createStmt =
                "CREATE TABLE Events (\n" +
                    "\teid TEXT NOT NULL PRIMARY KEY UNIQUE,\n" +
                    "\tpid TEXT NOT NULL,\n" +
                    "\tdescendant TEXT NOT NULL,\n" +
                    "\tevent_type TEXT NOT NULL,\n" +
                    "\tlatitude REAL NOT NULL,\n" +
                    "\tlongitude REAL NOT NULL,\n" +
                    "\tcountry TEXT NOT NULL,\n" +
                    "\tcity TEXT NOT NULL,\n" +
                    "\tyear TEXT NOT NULL,\n" +
                    "\tFOREIGN KEY(descendant) REFERENCES users(username),\n" +
                    "\tFOREIGN KEY(pid) REFERENCES Persons(pid)\n" +
                    ");";

    private static final String dropStmt =
                "DROP TABLE IF EXISTS Events";

    private static final String eventQuery =
                "SELECT *\n" +
                    "FROM Events\n" +
                    "WHERE eid = ?;";

    private static final String eventsQuery =
                "SELECT *\n" +
                    "FROM Events\n" +
                    "WHERE descendant = ?";

    private static final String insert =
                "INSERT INTO Events\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String deleteEvents =
                "DELETE FROM Events\n" +
                    "WHERE descendant = ?;";


    /**
     * Handles all Event interactions with the database
     *
     * @param connection an open database connection
     */
    public EventDAO(Connection connection) {
        super(connection);
    }

    /**
     * Creates the Events table in the database
     *
     * @throws DatabaseError if there is any problem performing the query
     */
    public void createTable() throws DatabaseError {
        createTable(createStmt, dropStmt);
    }

    /**
     * Selects an Event by event id
     *
     * @param eid the Event's id
     * @return the selected Event
     * @throws DatabaseError if there is any problem performing the query
     */
    public Event getEvent(String eid) throws DatabaseError {
        assert connection != null : "null connection";
        assert eid != null : "null eid";
        logger.info("Getting event: " + eid);
        Event output;

        try (PreparedStatement stmt = connection.prepareStatement(eventQuery)) {
            stmt.setString(1, eid);
            ResultSet rs = stmt.executeQuery();

            String eventId = rs.getString(1);
            String pid = rs.getString(2);
            String descendant = rs.getString(3);
            String event_type = rs.getString(4);
            double latitude = rs.getDouble(5);
            double longitude = rs.getDouble(6);
            String country = rs.getString(7);
            String city = rs.getString(8);
            String year = rs.getString(9);

            output = new Event(eventId, pid, descendant, event_type, latitude, longitude, country, city, year);
        }
        catch (SQLException e) {
            throw new DatabaseError("Event select failed " + e.getMessage());
        }

        return output;
    }

    /**
     * Selects all Events associated with a given Person
     *
     * @param descendant the user's id
     * @return an array of all Events associated with the Person
     * @throws DatabaseError if there is any problem performing the query
     */
    public List<Event> getEventsForUser(String descendant) throws DatabaseError {
        assert connection != null : "null connection";
        ArrayList<Event> output;

        try (PreparedStatement stmt = connection.prepareStatement(eventsQuery)) {
            assert descendant != null : "null descendant";
            stmt.setString(1, descendant);

            ResultSet rs = stmt.executeQuery();
            output = new ArrayList<>();

            while(rs.next()) {
                String eid = rs.getString(1);
                String pid = rs.getString(2);
                rs.getString(3);
                String event_type = rs.getString(4);
                double latitude = rs.getDouble(5);
                double longitude = rs.getDouble(6);
                String country = rs.getString(7);
                String city = rs.getString(8);
                String year = rs.getString(9);

                Event event = new Event(eid, pid, descendant, event_type, latitude, longitude, country,
                        city, year);
                output.add(event);
            }

        }
        catch (SQLException e) {
            throw new DatabaseError("Event select failed: " + e.getMessage());
        }

        return output;
    }

    /**
     * Inserts a given Event object into the database
     *
     * @param event the Event object to insert
     * @throws DatabaseError if there is any problem performing the query
     */
    public void insert(Event event) throws DatabaseError {
        assert connection != null : "null connection";

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            assert event != null : "null event";
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getPersonID());
            stmt.setString(3, event.getDescendant());
            stmt.setString(4, event.getEventType());
            stmt.setDouble(5, event.getLatitude());
            stmt.setDouble(6, event.getLongitude());
            stmt.setString(7, event.getCountry());
            stmt.setString(8, event.getCity());
            stmt.setString(9, event.getYear());

            if (stmt.executeUpdate() != 1) {
                throw new DatabaseError("insertEvent failed to execute");
            }
        }
        catch (SQLException e) {
            throw new DatabaseError("insertEvent failed:" + e.getMessage());
        }
    }

    /**
     * Deletes all Events from the database associated with a given Person
     *
     * @param username the Person's id
     * @throws DatabaseError if there is any problem performing the query
     */
    public void delete(String username) throws DatabaseError {
        assert username != null : "null username";
        logger.info("Deleting all events for " + username);
        assert connection != null : "null connection";

        try (PreparedStatement stmt = connection.prepareStatement(deleteEvents)) {
            stmt.setString(1, username);
            stmt.execute();
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new DatabaseError("delete failed");
        }

        logger.info("Events deleted successfully");
    }
}
