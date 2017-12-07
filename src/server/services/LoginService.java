package server.services;

import server.database.model.*;
import server.services.Request;
import server.services.Response;
import server.services.Service;

/**
 * Serves all login requests
 */
public class LoginService extends Service {

    /**
     * Logs in the user and returns an authToken
     *
     * @param req a JSON object containing the username and password
     * @return a JSON object containing the authToken
     */
    public LoginResponse login(LoginRequest req) {
        logger.info("Entering LoginService");

        if (openConnection()) {
            assert req != null : "null request";
            User user = getUser(req.getUserName(), req.getPassword());

            if (user != null) {
                AuthToken token = getNewToken(user);

                if (token != null) {

                    if (closeConnection(true)) {
                        logger.info("Login successful");
                        return new LoginResponse(token.getValue(), user.getUserName(),
                                user.getPersonID());
                    }
                }
            }

            closeConnection(false);
        } // connection opened

        logger.info("LoginService failed: " + error.getMessage());
        return null;
    }

    /**
     * The generic form of login()
     *
     * @param request (required) a LoginRequest object
     * @param user (not required)
     * @param parameter (not required)
     * @return a LoginResponse json object
     */
    @Override
    public Response getResponse(Request request, String user, String parameter) {
        assert request != null : "LoginService requires a LoginRequest";
        assert request.getClass() == LoginRequest.class : "request must be a LoginRequest";
        return login((LoginRequest)request);
    }
}
