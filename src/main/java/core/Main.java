package core;

import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {

    private static final NetworkChecker networkChecker = new NetworkChecker();

    public static void main(String[] args) {
        ResourceConfig rc = new ResourceConfig(RestService.class);

        URI endpoint = UriBuilder
                .fromUri("http://0.0.0.0/")
                .port(Integer.parseInt(System.getenv("PORT")))
                .build();

        GrizzlyHttpServerFactory.createHttpServer(endpoint, rc);
        networkChecker.start();
    }

}
