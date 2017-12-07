package server.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import server.services.FillService;

/**
 * Handles all "/fill" http requests
 */
public class FillHandler extends Handler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Entering FillHandler");
        boolean success = false;

        // require POST
        if (isPostRequest(httpExchange)) {
            String[] uriParts = getUriParts(httpExchange);

            // Check for required username parameter
            if (uriParts.length > URI_PARAM_INDEX) {
                String username = uriParts[URI_PARAM_INDEX];
                String generations = null;

                // Check for optional generations parameter
                if (uriParts.length > URI_PARAM_INDEX + 1) {
                    generations = uriParts[URI_PARAM_INDEX + 1];
                }

                FillService service = new FillService();
                success = processRequest(httpExchange, service, null, username, generations);
            }
        }

        if (!success) {
            sendBadRequestResponse(httpExchange);
        }

        logger.info("Exiting FillHandler\n");
    }
}
