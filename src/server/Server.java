/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.*;
import java.util.logging.*;
import server.handlers.*;

 /**
 The Server class is the "main" class for the server (i.e., it contains the
 "main" method for the server program).
 */
public class Server {

    // Static members
    private static final int MAX_WAITING_CONNECTIONS = 12;
    private static Logger logger;

    static {
        try {
            initLog();
        }
        catch (IOException e) {
            System.out.println("Could not initialize log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initLog() throws IOException {

        Level logLevel = Level.ALL;

        logger = Logger.getLogger("famServer");
        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);

        FileHandler fileHandler = new FileHandler("log.txt", false);
        fileHandler.setLevel(logLevel);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
    }

    // Non static members
    private HttpServer server;

    /**
     * "main" method for the server program
     *
     * @param args should contain one command-line argument, which is the port number
     *  on which the server should accept incoming client connections.
     */
    public static void main(String[] args) {

        if (args.length >= 1) {
            int portNumber = 0;

            try {
                portNumber = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                System.out.println("Usage: server portNumber");
                System.exit(0);
            }

            new Server().run(portNumber);
        }
        else {
            System.out.println("Usage: server portNumber");
            System.exit(0);
        }
    }

    /**
     * Initializes and runs the server.
     *
     * @param portNumber specifies the port number on which the server should accept incoming
     *  client connections.
     */
    private void run(int portNumber) {
        initializeServer(portNumber);
        createContexts();
        startServer();
    }

    /**
     * Initializes the server
     */
    private void initializeServer(int portNumber) {
        logger.info("Initializing HTTP Server");

        try {
            server = HttpServer.create(
                    new InetSocketAddress(portNumber), MAX_WAITING_CONNECTIONS);
        }
        catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // Indicate that we are using the default "executor" (necessary).
        server.setExecutor(null);

        logger.info("Finished Initialization");
    }

    /**
     * Creates server contexts (linking urls to Handlers)
     */
    private void createContexts() {
        logger.info("Creating contexts");

        // Default
        server.createContext("/", new WebHandler());

        // Registers new users
        server.createContext("/user/register", new RegisterHandler());

        // Logs in existing users
        server.createContext("/user/login", new LoginHandler());

        // Clears the database
        server.createContext("/clear", new ClearHandler());

        // Clears [username]'s data, then fills [username] with {generations} of random data
        server.createContext("/fill", new FillHandler());

        // Clears the database and loads new data given by the user
        server.createContext("/load", new LoadHandler());

        // Returns all people for the current user
        server.createContext("/person", new PersonHandler());

        // Returns events for the current user
        server.createContext("/event", new EventHandler());

        logger.info("Finished creating contexts");
    }

    private void startServer() {
        logger.info("Starting server");

        server.start();

        logger.log(Level.WARNING, "Server started\n");
    }
}
