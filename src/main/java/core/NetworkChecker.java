package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NetworkChecker {

    private final static Logger LOGGER = LoggerFactory.getLogger(NetworkChecker.class);

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void start() {
        executorService.scheduleAtFixedRate(this::checkInternetConnection, 5, 5, TimeUnit.SECONDS);
    }

    private void checkInternetConnection() {
        boolean reachable = false;
        try {
            InetAddress address = InetAddress.getByName("1.1.1.1");
            reachable = address.isReachable(5000);
        } catch (Exception e) {
            LOGGER.error("Internet check error", e);
        }

        LOGGER.info("Internet: " + (reachable ? "OK" : "Not OK"));
    }


}
