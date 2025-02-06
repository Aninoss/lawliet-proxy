package core;

import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@Path("")
@Singleton
public class RestService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestService.class);

    private final HttpClient httpClient = new HttpClient();
    private final Ratelimiter ratelimiter = new Ratelimiter(2_200_000_000L);

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
        return proxyWithHeader(url, auth, null);
    }

    @GET
    @Path("/proxy/{url}/{auth}/{header}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response proxyWithHeader(@PathParam("url") String url, @PathParam("auth") String auth, @PathParam("header") String header) throws InterruptedException {
        try {
            if (!System.getenv("AUTH").equals(auth)) {
                return Response.status(403).build();
            }

            ArrayList<String[]> headers = new ArrayList<>();
            if (url.startsWith("https://www.reddit.com")) {
                if (ratelimiter.peek() > 7000) {
                    LOGGER.warn("Reddit ratelimiter exceeds 7000 ms");
                    return Response.status(429).build();
                }
                if (header != null) {
                    headers.add(header.split(":"));
                }
                Thread.sleep(ratelimiter.nextRequestRelative());
            }

            HttpResponse httpResponse = httpClient.request(url, headers);
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
