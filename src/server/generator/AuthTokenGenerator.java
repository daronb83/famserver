package server.generator;

import server.database.model.AuthToken;
import server.database.model.User;

/**
 * Generates new authTokens with random values
 */
public class AuthTokenGenerator extends DataGenerator {

    /**
     * Generates a random AuthToken for a user
     *
     * @param user the user to generate a token for
     * @return the AuthToken
     */
    public AuthToken getAuthToken(User user) {
        assert user != null : "null user";
        logger.info("Generating token for " + user.getUid());

        return new AuthToken(uniqueId(), user.getUid());
    }
}
