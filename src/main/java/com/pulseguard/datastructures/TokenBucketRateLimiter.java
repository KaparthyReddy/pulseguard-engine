package com.pulseguard.datastructures;

/**
 * Token-bucket rate limiter used to cap how often a single pump can push
 * alerts/state changes, preventing alarm-fatigue floods.
 */
public class TokenBucketRateLimiter {
    private final double capacity;
    private final double refillRatePerSecond;
    private double availableTokens;
    private long lastRefillNanos;

    public TokenBucketRateLimiter(double capacity, double refillRatePerSecond) {
        if (capacity <= 0 || refillRatePerSecond <= 0) {
            throw new IllegalArgumentException("Capacity and refill rate must be positive");
        }
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.availableTokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (availableTokens >= 1.0) {
            availableTokens -= 1.0;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
        double newTokens = elapsedSeconds * refillRatePerSecond;
        if (newTokens > 0) {
            availableTokens = Math.min(capacity, availableTokens + newTokens);
            lastRefillNanos = now;
        }
    }

    public synchronized double getAvailableTokens() {
        refill();
        return availableTokens;
    }
}
