package core;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    public static final String USER_AGENT = "Lawliet Discord Bot by aninoss";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .build();

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
            LOGGER.error("Web error ({})", domain, e);
            return new HttpResponse()
                    .setCode(500);
        }
    }

}
