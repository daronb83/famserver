package server.services;

import java.util.UUID;
import java.util.logging.Level;

import server.database.DatabaseError;
import server.database.access.PersonDAO;
import server.database.access.UserDAO;
import server.database.model.AuthToken;
import server.database.model.Person;
import server.database.model.User;

/**
 * Serves all registration requests
 */
public class RegisterService extends Service {

    /**
     * Creates a new user account, generates 4 generations of ancestor data for the new
     *  user, logs the user in, and returns an auth token.
     *
     * @param req a JSON object containing the required registration data
     * @return a JSON object containing an AuthToken or error description
     */
    public LoginResponse register(RegisterRequest req) {
        logger.info("Entering RegisterService");

        if (openConnection()) {

            if (uniqueUserName(req.getUserName())) {
                String pid = UUID.randomUUID().toString();
                Person person = new Person(pid, req.getUserName(), req.getFirstName(),
                                           req.getLastName(), req.getGender());

                String uid = UUID.randomUUID().toString();
                User user = new User(uid, pid, req.getUserName(), req.getPassword(), req.getEmail());

                if (insertUser(user) && insertPerson(person)) {
                    logger.info("New user and person inserted successfully");

                    if (generateData(user, DEFAULT_GENERATIONS) != null) {
                        AuthToken token = getNewToken(user);

                        if (token != null) {

                            if (closeConnection(true)) {
                                logger.info("RegisterService completed successfully");
                                return new LoginResponse(token.getValue(), user.getUserName(), pid);
                            }
                        }
                    }
                }
            }
            closeConnection(false);
        }

        logger.log(Level.WARNING, "RegisterService failed: " + error.getMessage());
        return null;
    }

    private boolean uniqueUserName(String username) {
        logger.info("Checking username uniqueness");
        UserDAO uDAO = db.getUserDAO();

        try {
            if (uDAO.getUserByUsername(username) != null) {
                error = new MessageResponse("Username already taken by another user");
                return false;
            }
        }
        catch (DatabaseError e) {
            logger.info(e.getMessage());
        }

        logger.info("Username is unique");
        return true;
    }

    private boolean insertPerson(Person person) {
        PersonDAO pDao = db.getPersonDAO();

        try {
            pDao.insert(person);
            return true;
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage());
            error = new MessageResponse("Property missing or has invalid value");
        }
        return false;
    }

    private boolean insertUser(User user) {
        UserDAO uDao = db.getUserDAO();

        try {
            uDao.insert(user);
            return true;
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage());
            error = new MessageResponse("Property missing or has invalid value");
        }
        return false;
    }

    /**
     * The generic form of register()
     *
     * @param request (required) a RegisterRequest object
     * @param user (not required)
     * @param parameter (not required)
     * @return a LoginResponse json object
     */
    @Override
    public Response getResponse(Request request, String user, String parameter) {
        assert request != null : "RegisterService requires a RegisterRequest";
        assert request.getClass() == RegisterRequest.class : "request must be a RegisterRequest";
        logger.info("getResponse: Register Service");
        return register((RegisterRequest)request);
    }
}
