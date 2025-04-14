package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ratelimiter {

    private final static Logger LOGGER = LoggerFactory.getLogger(Ratelimiter.class);

    private final long intervalTimeNanos;
    private long nextRequest = 0;
    private int requestCount = 0;

    public Ratelimiter(long intervalTimeNanos) {
        this.intervalTimeNanos = intervalTimeNanos;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            LOGGER.info("{} reddit requests per minute", requestCount);
            requestCount = 0;
        }, 1, 1, TimeUnit.MINUTES);
    }

    public synchronized int nextRequestRelative() {
        long currentTime = System.nanoTime();
        long waitingTime = Math.max(0, nextRequest - currentTime);
        nextRequest = Math.max(currentTime + intervalTimeNanos, nextRequest + intervalTimeNanos);
        requestCount++;
        return (int) (waitingTime / 1_000_000);
    }

    public synchronized int peek() {
        long currentTime = System.nanoTime();
        long waitingTime = Math.max(0, nextRequest - currentTime);
        return (int) (waitingTime / 1_000_000);
    }

}
