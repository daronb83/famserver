package server.services;

import server.database.DatabaseError;

/**
 * Clears all data from the database
 */
public class ClearService extends Service {

    /**
     * Clears all data from the database
     *
     * @return a JSON response detailing the success or failure
     */
    public MessageResponse clear() throws DatabaseError {
        logger.info("Entering ClearService");

        if (openConnection()) {

            if (clearDatabase()) {

                if (closeConnection(true)) {
                    logger.info("ClearService succeeded");
                    return new MessageResponse("ClearService succeeded");
                }
            }

            closeConnection(false);
        }

        logger.info("ClearService Failed: " + error.getMessage());
        return null;
    }

    /**
     * The generic form of clear()
     *
     * @param request (not required)
     * @param user (not required)
     * @param parameter (not required)
     * @return
     */
    @Override
    public Response getResponse(Request request, String user, String parameter) {

        return clear();
    }
}
