package core;

import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("")
@Singleton
public class RestService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestService.class);

    private final HttpClient httpClient = new HttpClient();
    private final Ratelimiter ratelimiter = new Ratelimiter(2_100_000_000L);

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "Pong!";
    }

    @GET
    @Path("/proxy/{url}/{auth}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response proxy(@PathParam("url") String url, @PathParam("auth") String auth) throws InterruptedException {
        try {
            if (!System.getenv("AUTH").equals(auth)) {
                return Response.status(403).build();
            }
            if (url.startsWith("https://www.reddit.com")) {
                Thread.sleep(ratelimiter.nextRequestRelative());
            }

            HttpResponse httpResponse = httpClient.request(url);
            if (httpResponse.getCode() / 100 == 2) {
                return Response.ok(httpResponse.getBody()).build();
            } else {
                LOGGER.warn("Proxy: error response {} for url {}", httpResponse.getCode(), url);
                return Response.status(httpResponse.getCode()).build();
            }
        } catch (Throwable e) {
            LOGGER.error("Error in /proxy", e);
            throw e;
        }
    }

}
