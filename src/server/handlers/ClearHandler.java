package server.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

import server.services.ClearService;

/**
 * Handles all "/clear" http requests
 */
public class ClearHandler extends Handler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Entering ClearHandler");
        boolean success = false;

        // require POST
        if (isPostRequest(httpExchange)) {
            success = processRequest(httpExchange, new ClearService(), null, null, null);
        }

        if (!success) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.getResponseBody().close();
        }

        logger.info("Exiting ClearHandler\n");
    }
}
