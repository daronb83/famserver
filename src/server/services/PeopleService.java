package server.services;

import server.database.model.Person;
import server.database.model.User;
import server.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import server.database.access.PersonDAO;

/**
 * Serves all person requests with no specified ID
 */
public class PeopleService extends Service {

    /**
     * Returns ALL family members of the current user. The current user is
     * determined from the provided auth token.
     *
     * @param tokenValue the current user's AuthToken value
     * @return a JSON object containing an array of Person objects
     */
    public PeopleResponse getPeople(String tokenValue) {

        if (openConnection()) {
            assert tokenValue != null : "null tokenValue";
            User user = getUserByTokenValue(tokenValue);

            if (user != null) {
                List<Person> people = getPeople(user);
                List<PersonResponse> responses = getResponses(people);
                closeConnection(true);
                logger.info("PeopleService completed successfully");
                return new PeopleResponse(responses);
            }
            closeConnection(false);
        }

        logger.log(Level.WARNING, "PeopleService failed: " + error.getMessage());
        return null;
    }

    private List<Person> getPeople(User user) {
        assert user != null : "null user";
        PersonDAO pDao = db.getPersonDAO();

        try {
            return pDao.getPersonsByUsername(user.getUserName());
        }
        catch (DatabaseError e) {
            logger.log(Level.WARNING, e.getMessage());
            error = new MessageResponse("Invalid input something something");
        }

        return null;
    }

    private List<PersonResponse> getResponses(List<Person> people) {
        List<PersonResponse> responses = new ArrayList<>();
        assert people != null : "null people";

        for (Person person : people) {
            PersonResponse response = new PersonResponse(
                    person.getDescendant(), person.getPersonID(), person.getFirstName(),
                    person.getLastName(), person.getGender(), person.getFather(),
                    person.getMother(), person.getSpouse()
            );

            responses.add(response);
        }

        return responses;
    }

    /**
     * The generic form of getPeople()
     *
     * @param request (not required)
     * @param user (required) an AuthToken value
     * @param parameter (not required)
     * @return a PeopleResponse json object
     */
    @Override
    public Response getResponse(Request request, String user, String parameter) {
        assert user != null : "PeopleService requires an AuthToken value";
        return getPeople(user);
    }
}
