package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.*;

/**
 * Serves the test website
 */
public class WebHandler implements HttpHandler {

    private static Logger logger;

    static {
        logger = Logger.getLogger("famServer");
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Entering WebHandler");

        String filePathStr = "web" + httpExchange.getRequestURI().toString();

        if (filePathStr.equals("web/")) {
            filePathStr = "web/index.html";
        }

        boolean success = false;

        try {
            Path filePath = FileSystems.getDefault().getPath(filePathStr);
            logger.info("Full Path: " + filePath.toUri().toString());

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            Files.copy(filePath, httpExchange.getResponseBody());
            httpExchange.getResponseBody().close();

            success = true;
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            httpExchange.getResponseBody().close();
        }

        if (!success) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.getResponseBody().close();
        }


        logger.info(("Exiting WebHandler\n"));
    }
}
