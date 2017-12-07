package server.services;

import java.util.logging.Level;

import server.database.DatabaseError;
import server.database.access.EventDAO;
import server.database.access.PersonDAO;
import server.database.access.UserDAO;
import server.database.model.User;

/**
 * Serves all fill requests
 */
public class FillService extends Service {

    /**
     * Populates the database with random information for the given user, deleting all previously
     *  existing information
     *
     * @param userName the username of the user to fill
     * @param generations the number of generations to fill
     * @return a JSON response detailing the success or failure
     */
    public MessageResponse fill(String userName, int generations) {
        logger.info("Entering FillService");

        if (openConnection()) {
            assert userName != null : "null username";
            User user = getUser(userName);

            if (user != null) {
                assert generations >= 0 : "invalid generations param";
                clearUserData(user);
                MessageResponse response = generateData(user, generations);

                if (response != null) {

                    if (closeConnection(true)) {
                        logger.info("FillService completed successfully");
                        return response;
                    }
                }
            }
            closeConnection(false);
        }

        logger.log(Level.WARNING, "FillService failed: " + error.getMessage());
        return null;
    }

    /**
     * Returns a User object from a username
     *
     * @param username the user's username
     * @return the User object
     */
    private User getUser(String username) {

        try {
            UserDAO userDAO = db.getUserDAO();
            User user =  userDAO.getUserByUsername(username);

            if (user.getUid() != null) {
                return user;
            }
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        error = new MessageResponse("Invalid username or generations parameter");
        return null;
    }

    private boolean clearUserData(User user) {
        logger.info("Clearing user data for " + user.getUserName());
        PersonDAO pDao = db.getPersonDAO();
        EventDAO eDao = db.getEventDAO();

        try{
            eDao.delete(user.getUserName());
            pDao.delete(user);
            return true;
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            error = new MessageResponse("Invalid username or generations parameter");
            return false;
        }
    }

    /**
     * The generic form of fill()
     *
     * @param request (not required)
     * @param user (required) a user's username
     * @param parameter (optional, must be an int >= 0) the number of generations
     * @return a MessageResponse json object
     */
    @Override
    public Response getResponse(Request request, String user, String parameter) {

        // Check for optional generations parameter
        if (parameter != null) {
            try {
                int generations = Integer.parseInt(parameter);

                if (generations >= 0) {
                    return fill(user, generations);
                }
            }
            catch (NumberFormatException e) {
                logger.log(Level.WARNING, e.getMessage(), e);

            }
        }
        else {
            return fill(user, DEFAULT_GENERATIONS);
        }

        logger.log(Level.WARNING, "FillService failed");
        error = new MessageResponse("Invalid username or generations parameter");
        return null;
    }

}
