// Imporing
package com.smartcampus.application;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String BASE_URI = "http://localhost:8080/api/v1/"; // defining the base URL

    public static HttpServer startServer() {
        ResourceConfig config = ResourceConfig.forApplication(new SmartCampusApplication());
        config.register(JacksonFeature.class); // enabling JSON output

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = startServer();
        
        // Loggers to display the output
        LOGGER.info("Server started at: " + BASE_URI);
        LOGGER.info("Discovery endpoint: " + BASE_URI + "api/v1");
        LOGGER.info("Press Enter to shutdown the server"); 

        System.in.read(); // waiting for user input to shutdown the server
        server.shutdownNow(); // shutting down the server
    }
}