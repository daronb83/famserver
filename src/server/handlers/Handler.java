package server.handlers;

import server.services.Service;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.json.JsonDecoder;
import server.json.JsonEncoder;
import server.services.Request;
import server.services.Response;
import server.services.*;

/**
 * Code reuse for Handler classes
 */
abstract class Handler implements HttpHandler {

    /* STATIC */
    /**
     * The location where URI parameters start after calling getUriParts()
     */
    static final int URI_PARAM_INDEX = 2;
    static Logger logger;

    static {
        logger = Logger.getLogger("famServer");
    }


    /* NON-STATIC PROTECTED METHODS */

    /**
     * Returns true if this is a GET request
     * @param httpExchange an httpExchange
     * @return true if this is a GET request
     */
    boolean isGetRequest(HttpExchange httpExchange) {
        logger.info("Request method: " + httpExchange.getRequestMethod());
        return httpExchange.getRequestMethod().toLowerCase().equals("get");
    }

    /**
     * Returns true if this is a POST request
     * @param httpExchange an httpExchange
     * @return true if this is a POST request
     */
    boolean isPostRequest(HttpExchange httpExchange) {
        logger.info("Request method: " + httpExchange.getRequestMethod());
        return httpExchange.getRequestMethod().toLowerCase().equals("post");
    }

    /**
     * Returns the AuthToken value from the exchange headers if it exists
     * @param httpExchange an httpExchange
     * @return AuthToken value or null
     */
    String getAuthTokenValue(HttpExchange httpExchange) {
        Headers reqHeaders = httpExchange.getRequestHeaders();

        // Check for Authorization header
        if (reqHeaders.containsKey("Authorization")) {
            String token = reqHeaders.getFirst("Authorization");
            logger.info("AuthToken: " + token);
            return token;
        }

        logger.log(Level.WARNING, "Missing AuthToken");
        return null;
    }

    /**
     * Splits the exchange's URI request into its composite strings
     *
     * @param httpExchange an httpExchange
     * @return an array of the URI request's composite strings, with the format:
     *
     *              uriParts[0]: null (the leading '/')
     *              uriParts[1]: the base service name (ie: 'fill')
     *              uriParts[2-i]: parameters
     */
    String[] getUriParts(HttpExchange httpExchange) {
        String uri = httpExchange.getRequestURI().toString();
        logger.info("URI: " + uri);
        return uri.split("/");
    }

    /**
     * Fills a json Request object with data from the HTTP request body
     *
     * @param httpExchange an httpExchange
     * @param request the correct type of request object to fill
     * @return the filled request object
     */
    Request getRequestData(HttpExchange httpExchange, Request request) {
        JsonDecoder decoder = new JsonDecoder();
        InputStream jsonIn = httpExchange.getRequestBody();
        return (Request)decoder.decodeStream(jsonIn, request);
    }

    /**
     * Processes the provided service and returns that service's response object
     *
     * @param httpExchange an httpExchange
     * @param service the service that should handle the request
     * @param request a json request object (not always required)
     * @param user a username or authToken value (not always required)
     * @param param a parameter (not always required)
     * @return the response from the service
     */
    boolean processRequest(HttpExchange httpExchange, Service service, Request request, String user,
                           String param) throws IOException {

        logger.info("Processing request");

        try {
            Response response = service.getResponse(request, user, param);

            // Send response
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            JsonEncoder encoder = new JsonEncoder();

            if (response != null) {
                encoder.encodeToStream(response, httpExchange.getResponseBody());
                httpExchange.getResponseBody().close();
            }
            else {
                encoder.encodeToStream(service.getError(), httpExchange.getResponseBody());
                httpExchange.getResponseBody().close();
            }

            logger.info("Request processed successfully");
            return true;
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            httpExchange.getResponseBody().close();
            return false;
        }
    }

    /**
     * Sends an HTTP error for a bad request
     *
     * @param httpExchange an httpExchange
     * @throws IOException if the exchange is not successfully sent or closed
     */
    void sendBadRequestResponse(HttpExchange httpExchange) throws IOException{
        logger.log(Level.WARNING, "Bad Request");
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        httpExchange.getResponseBody().close();
    }
}
