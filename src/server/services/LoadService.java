package server.services;

import server.database.access.UserDAO;
import server.database.model.Person;
import server.database.model.Event;
import server.database.model.User;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import server.database.DatabaseError;

/**
 * Serves all Load requests
 */
public class LoadService extends Service{

    /**
     * Clears all data from the database (just like the /clear API), and then loads the
     *  posted user, person, and event data into the database
     *
     * @param request a JSON request object containing the users, persons and events to load
     * @return a JSON response detailing success or failure
     */
    public MessageResponse load(LoadRequest request) {
        logger.info("Entering LoadService");

        if (openConnection()) {

            if (clearDatabase()) {

                if (loadData(request)) {

                    if (closeConnection(true)) {
                        logger.info("LoadService completed successfully");
                        return sendResponse(request);
                    }
                }
            }
            closeConnection(false);
        }

        logger.log(Level.WARNING, "LoadService failed: " + error.getMessage());
        return null;
    }

    private boolean loadData(LoadRequest request) {
        List<Person> persons = request.getPersons();
        List<User> users = request.getUsers();
        List<Event> events = request.getEvents();

        cleanUsers(users);
        cleanEvents(events, persons);

        return loadUsers(users) && loadPersons(persons) && loadEvents(events);
    }

    private void cleanUsers(List<User> users) {

        for (User user : users) {
            logger.info("Cleaning User: " + user.getUserName());
            user.setUid(UUID.randomUUID().toString());
        }
    }

    private void cleanEvents(List<Event> events, List<Person> persons) {

        for (Event event : events) {

            for (Person person : persons) {

                if (event.getPersonID().equals(person.getPersonID())) {
                    event.setDescendant(person.getDescendant());
                    break;
                }
            }
        }
    }

    private boolean loadUsers(List<User> users) {
        logger.info("Loading users");
        UserDAO uDao = db.getUserDAO();

        try {
            for (User user : users) {
                uDao.insert(user);
            }

            logger.info("Users loaded successfully");
            return true;
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }

    private MessageResponse sendResponse(LoadRequest request) {
        int uSize = request.getUsers().size();
        int pSize = request.getPersons().size();
        int eSize = request.getEvents().size();

        String message = "Successfully added " + uSize + " users, " + pSize +
                " persons, and " + eSize + " events to the database.";

        logger.info(message);
        return new MessageResponse(message);
    }

    /**
     * The generic form of load()
     *
     * @param request (required) a LoadRequest object
     * @param user (not required)
     * @param parameter (not required)
     * @return a MessageResponse json object
     */
    @Override
    public Response getResponse(Request request, String user, String parameter) {
        assert request != null : "LoadService requires a LoadRequest";
        assert request.getClass() == LoadRequest.class : "request must be a LoadRequest";
        return load((LoadRequest)request);
    }
}
