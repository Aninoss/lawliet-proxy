package core;

public class Ratelimiter {

    private final long intervalTimeNanos;
    private long nextRequest = 0;

    public Ratelimiter(long intervalTimeNanos) {
        this.intervalTimeNanos = intervalTimeNanos;
    }

    public synchronized int nextRequestRelative() {
        long currentTime = System.nanoTime();
        long waitingTime = Math.max(0, nextRequest - currentTime);
        nextRequest = Math.max(currentTime + intervalTimeNanos, nextRequest + intervalTimeNanos);
        return (int) (waitingTime / 1_000_000);
    }

}
