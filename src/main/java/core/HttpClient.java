package core;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    public static final String USER_AGENT = "Lawliet Discord Bot by aninoss";

    private final OkHttpClient client;

    public HttpClient() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(999);
        dispatcher.setMaxRequestsPerHost(999);
        ConnectionPool connectionPool = new ConnectionPool(100, 5, TimeUnit.MINUTES);
        Dns dns = hostname -> Arrays.asList(InetAddress.getAllByName(hostname));

        this.client = new OkHttpClient.Builder()
                .dns(dns)
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .protocols(List.of(Protocol.HTTP_1_1))
                .build();
    }

    public HttpResponse request(String url, List<String[]> headers) {
        String domain = url.split("/")[2];
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT);

        for (String[] header : headers) {
            requestBuilder.header(header[0], header[1]);
        }

        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            return new HttpResponse()
                    .setCode(response.code())
                    .setBody(response.body().string());
        } catch (Throwable e) {
            if (!(e instanceof InterruptedIOException)) {
                LOGGER.error("Web error ({})", domain, e);
            }
            return new HttpResponse()
                    .setCode(500);
        }
    }

}
