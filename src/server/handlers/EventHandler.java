package server.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import server.services.EventService;
import server.services.EventsService;

/**
 * Handles all "/event" requests
 */
public class EventHandler extends Handler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Entering EventHandler");
        boolean success = false;

        // Only allow GET requests
        if (isGetRequest(httpExchange)) {
            String token = getAuthTokenValue(httpExchange);

            if (token != null) {
                String[] uriParts = getUriParts(httpExchange);

                // EventService requires a parameter, while EventsService does not
                if (uriParts.length > 2) { // Event
                    String eid = uriParts[2];
                    success = processRequest(httpExchange, new EventService(), null, token, eid);
                }
                else { // Events
                    success = processRequest(httpExchange, new EventsService(), null, token, null);
                }
            }
        } // isGetRequest()

        if (!success) {
            sendBadRequestResponse(httpExchange);
        }

        logger.info("Leaving EventHandler\n");
    }

}
