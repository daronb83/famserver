package server.handlers;

import server.services.LoadRequest;
import server.services.LoadService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import server.services.Request;

/**
 * Handles all "/load" http requests
 */
public class LoadHandler extends Handler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Entering LoadHandler");
        boolean success = false;

        // Only allow POST requests
        if (isPostRequest(httpExchange)) {
            Request request = getRequestData(httpExchange, new LoadRequest());
            success = processRequest(httpExchange, new LoadService(), request, null, null);

        }

        if (!success) {
            sendBadRequestResponse(httpExchange);
        }

        logger.info("Leaving LoadHandler\n");
    }
}
