package server.services;

import server.database.model.Person;
import server.database.model.User;
import java.util.logging.Level;


/**
 * Serves all Person requests
 */
public class PersonService extends Service {

    /**
     * Returns the single Person object with the specified ID
     *
     * @param tokenValue the current user's AuthToken
     * @param personID the ID of the specified Person
     * @return a JSON response containing the specified Person object
     */
    public PersonResponse getPerson(String tokenValue, String personID) {
        logger.info("Entering Person Service");

        if (openConnection()) {
            assert tokenValue != null : "null token value";
            User user = getUserByTokenValue(tokenValue);

            if (user != null) {
                assert personID != null : "null person id";
                Person person = getPerson(personID);
                Person descendant = getPerson(user.getPersonID());

                if (person != null && descendant != null) {

                    // ensure the Person belongs to this user
                    if (person.getDescendant().equals(user.getUserName())) {
                        logger.info("Person retrieved successfully");
                        closeConnection(true);

                        return new PersonResponse(
                                user.getUserName(), person.getPersonID(), person.getFirstName(),
                                person.getLastName(), person.getGender(), person.getFather(),
                                person.getMother(), person.getSpouse()
                        );
                    } // person belongs to user?
                    else {
                        error = new MessageResponse("Person does not belong to this user");
                        logger.log(Level.WARNING, error.getMessage());
                    }
                } // person & descendant exit?
            } // user exists?

            closeConnection(false);
        } // connection opened?

        logger.log(Level.WARNING, "PersonService failed: " + error.getMessage());
        return null;
    }

    /**
     * The generic form of getPerson()
     *
     * @param request (not required)
     * @param user (required) an Authtoken value representing a user
     * @param parameter (required) the personId of the requested person
     * @return a PersonResponse json object
     */
    @Override
    public Response getResponse(Request request, String user, String parameter) {
        assert user != null : "PersonService requires an AuthToken value";
        assert parameter != null : "PersonService requires a PersonId value";
        return getPerson(user, parameter);
    }
}
