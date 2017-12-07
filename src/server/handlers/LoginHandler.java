package server.handlers;

import com.sun.net.httpserver.*;

import java.io.*;

import server.services.*;

/**
 * Handles all "/user/login" HTTP requests
 */
public class LoginHandler extends Handler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Entering LoginHandler");
        boolean success = false;

        // require POST request
        if (isPostRequest(httpExchange)) {
            Request request = getRequestData(httpExchange, new LoginRequest());
            success = processRequest(httpExchange, new LoginService(), request, null, null);
        }

        if (!success) {
            sendBadRequestResponse(httpExchange);
        }

        logger.info("Leaving LoginHandler\n");
    }
}
