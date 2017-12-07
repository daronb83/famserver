package server.services;

import server.database.access.EventDAO;
import server.database.model.Event;
import server.database.model.User;
import server.database.DatabaseError;
import java.util.logging.Level;

/**
 * Serves all event requests with a specified event ID
 */
public class EventService extends Service {

    /**
     * Returns the single Event object with the specified ID
     *
     * @param tokenValue the user's AuthToken value
     * @param eventId the event's ID
     * @return a JSON object containing the event, or null if there was an error (Call getError()
     *  to retrieve the error response)
     */
    public EventResponse getEvent(String tokenValue, String eventId) {
        logger.info("Entering EventService");

        if (openConnection()) {
            assert tokenValue != null : "null tokenValue";
            User user = getUserByTokenValue(tokenValue);

            if (user != null) {
                assert eventId != null : "null eventID";
                Event event = getEvent(user, eventId);

                if (event != null) {
                    closeConnection(true);
                    logger.info("EventService completed successfully");
                    return new EventResponse(
                            user.getUserName(), event.getEventID(), event.getPersonID(), event.getLatitude(),
                            event.getLongitude(), event.getCountry(), event.getCity(),
                            event.getEventType(), event.getYear()
                    );
                } // event exists and belongs to user?
            } // user exists?

            closeConnection(false);
        } // connection opened?

        logger.log(Level.WARNING, "EventService failed: " + error.getMessage());
        return null;
    }

    private Event getEvent(User user, String eventId) {
        Event event = null;

        try {
            assert db.connectionOpen() : "no database connection";
            EventDAO eDao = db.getEventDAO();

            assert eventId != null : "null eid";
            assert user != null : "null user";

            event = eDao.getEvent(eventId);

            if (event.getEventID() != null) {

                if (event.getDescendant().equals(user.getUserName())) {
                    return event;
                }
                else {
                    error = new MessageResponse("Event does not belong to this user");
                    return null;
                }
            }
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        error = new MessageResponse("Invalid eventID parameter");
        return null;
    }

    /**
     * The generic form of getEvent()
     *
     * @param request (not required)
     * @param user (required) an AuthToken value representing a user
     * @param parameter (required) the event's id
     * @return
     */
    @Override
    public Response getResponse(Request request, String user, String parameter) {
        assert user != null : "EventService requires an AuthToken value";
        assert parameter != null : "EventService requires an eventId parameter";
        return getEvent(user, parameter);
    }
}
