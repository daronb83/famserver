package server.services;

import server.database.access.PersonDAO;
import server.database.access.UserDAO;
import server.database.access.EventDAO;
import server.database.access.AuthTokenDAO;
import server.database.model.AuthToken;
import server.database.model.Person;
import server.database.model.Event;
import server.database.model.User;
import server.database.DatabaseError;
import server.database.Database;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.database.*;
import server.database.access.*;
import server.database.model.*;
import server.generator.AuthTokenGenerator;
import server.generator.FamilyTreeGenerator;

/**
 * Represents a service that the server is capable of performing
 * Contains general use methods for services to inherit
 */
public abstract class Service {

    /* STATIC */
    protected static Logger logger;
    protected static final int DEFAULT_GENERATIONS = 4;

    static {
        logger = Logger.getLogger("famServer");
    }

    /* NON-STATIC */

    protected MessageResponse error;
    protected Database db;

    /* PUBLIC METHODS */

    /**
     * Represents a service that the server is capable of performing
     * Contains general use methods for services to inherit
     */
    public Service(){
        db = new Database();
        error = new MessageResponse("Error was not initialized");
    }

    /**
     * Generic method for retrieving a service response
     *
     * @param request a json Request object, if required
     * @param user a string representing a user (username or authToken), if required
     * @param parameter a parameter value, if required
     * @return a json Response object
     */
    public abstract Response getResponse(Request request, String user, String parameter);

    /**
     * Overloaded for when String params are not required
     */
    public Response getResponse(Request request) {
        return getResponse(request, null, null);
    }

    /**
     * Overloaded for when a Request is not required
     */
    public Response getResponse(String user, String parameter) {
        return getResponse(null, user, parameter);
    }

    /**
     * Returns a JSON object containing an error message response
     *  Call this if the primary service method returns null.
     *
     * @return a JSON object containing an error message response
     */
    public MessageResponse getError() {
        return error;
    }


    /* PROTECTED METHODS */

    /**
     * Attempts to open a database connection and starts a new transaction
     *
     * @return true if the connection is opened successfully
     */
    protected boolean openConnection() {

        if (db.connectionOpen()) {
            // Connection already open, just return true
            return true;
        }

        try {
            db.openConnection();
        }
        catch (DatabaseError e) {
            e.printStackTrace();
        }

        if (db.connectionOpen()){
            return true;
        }
        else {
            error = new MessageResponse("Internal Server Error");
            logger.log(Level.WARNING, error.getMessage());

            return false;
        }
    }

    /**
     * Attempts to close a database connection
     *
     * @param commit whether or not to commit the changes made during this transaction
     * @return true if the connection was closed successfully
     */
    protected boolean closeConnection(boolean commit) {
        assert db.connectionOpen() : "no database connection";

        try {
            db.closeConnection(commit);
            return true;
        }
        catch (DatabaseError e) {
            e.printStackTrace();
            error = new MessageResponse("Internal Server Error");
            logger.log(Level.WARNING, error.getMessage());
            return false;
        }
    }

    /**
     * Drops and then recreates all database tables, used by LoadService and ClearService
     *
     * @return true if the operation was successful
     */
    protected boolean clearDatabase() {
        logger.info("Clearing Database");

        try {
            db.getAuthTokenDAO().createTable();
            db.getEventDAO().createTable();
            db.getPersonDAO().createTable();
            db.getUserDAO().createTable();

            logger.info("Clear Succeeded");
            return true;
        }
        catch (DatabaseError e) {
            error = new MessageResponse("Internal server error");
            logger.log(Level.WARNING, error.getMessage());
            return false;
        }
    }

    /**
     * Gets a user by username and password
     *
     * @param username the user's username
     * @param password the user's password
     * @return the user
     */
    protected User getUser(String username, String password) {

        try {
            UserDAO userDAO = db.getUserDAO();
            return userDAO.login(username, password);
        }
        catch (DatabaseError e) {
            error = new MessageResponse("Request property missing or has invalid value");
            logger.log(Level.WARNING, error.getMessage());
            return null;
        }
    }

    /**
     * Returns the user associated with an AuthToken (used by many)
     *
     * @param tokenValue the token's unique value
     * @return the User associated with the token, if it exists
     */
    protected User getUserByTokenValue(String tokenValue) {
        assert db.connectionOpen() : "no database connection";

        AuthTokenDAO atDao = db.getAuthTokenDAO();
        UserDAO uDao = db.getUserDAO();

        try {
            assert tokenValue != null : "null token value";
            AuthToken token = atDao.getToken(tokenValue);
            User user = uDao.getUserById(token.getUid());

            if (user != null) {
                return user;
            }
        }
        catch (DatabaseError e) {
            e.printStackTrace();
        }

        error = new MessageResponse("Invalid auth token");
        logger.log(Level.WARNING, error.getMessage());

        return null;
    }

    /**
     * Returns a person from the database by personID
     *
     * @param personID the id of the person to get
     * @return the person
     */
    protected Person getPerson(String personID) {
        Person person = null;
        PersonDAO pDao = db.getPersonDAO();

        try {
            person = pDao.getPerson(personID);
        }
        catch (DatabaseError e) {
            e.printStackTrace();
        }

        if (person != null) {
            return person;
        }

        error = new MessageResponse("Invalid personID parameter");
        logger.log(Level.WARNING, error.getMessage());
        return null;
    }

    /**
     * Generates an authToken for the Register and Login services
     *
     * @param user the user to generate a token for
     * @return the authToken
     */
    protected AuthToken getNewToken(User user) {
        assert user != null : "null user";
        AuthTokenGenerator generator = new AuthTokenGenerator();
        AuthToken token = generator.getAuthToken(user);

        try {
            AuthTokenDAO atDao = db.getAuthTokenDAO();
            atDao.insertToken(token);
        }
        catch (DatabaseError e) {
            error = new MessageResponse("Internal Server Error");
            logger.log(Level.WARNING, error.getMessage());
            return null;
        }

        return token;
    }

    /**
     * Inserts fake data into the database for the specified user
     *
     * @param user the user to generated data for
     * @param generations the number of generations of data to insert
     * @return a success MessageResponse
     */
    protected MessageResponse generateData(User user, int generations) {
        assert generations >= 0 : "invalid generations param";
        logger.info("Inserting " + generations + " generations of random data");

        assert user != null : "null user";
        Person descendant = getPerson(user.getPersonID());

        if (descendant != null) {
            FamilyTreeGenerator tree = new FamilyTreeGenerator();
            descendant = tree.generateTree(descendant, generations);
            List<Person> persons = tree.getPersonList();
            List<Event> events = tree.getEventList();

            if (update(descendant) && loadPersons(persons) && loadEvents(events)) {
                String message = "Successfully added " + persons.size() + " persons and " +
                        events.size() + " events to the database";

                logger.info(message);
                return new MessageResponse(message);
            }
        }

        logger.log(Level.WARNING, "InsertGeneratedData failed");
        return null;
    }

    /**
     * Updates a person's IDs
     */
    private boolean update(Person descendant) {
        logger.info("Updating " + descendant.getPersonID());
        PersonDAO pDao = db.getPersonDAO();

        try {
            pDao.updateIDs(descendant);
            return true;
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }


    /**
     * Inserts a list of person objects into the database
     *
     * @param persons the list of person objects
     * @return true if successful
     */
    protected boolean loadPersons(List<Person> persons) {
        logger.info("Loading persons");
        PersonDAO pDao = db.getPersonDAO();

        try {

            for (Person person : persons) {
                pDao.insert(person);
            }

            logger.info("Persons loaded successfully");
            return true;
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Inserts a list of event objects into the database
     *
     * @param events the list of event objects
     * @return true if successful
     */
    protected boolean loadEvents(List<Event> events) {
        logger.info("Loading events");
        EventDAO eDao = db.getEventDAO();

        try {
            for (Event event : events) {
                eDao.insert(event);
            }

            logger.info("Events loaded successfully");
            return true;
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }

}
