package com.pulseguard.engine;

import com.pulseguard.core.InfusionPump;
import com.pulseguard.core.PumpState;
import com.pulseguard.datastructures.TokenBucketRateLimiter;
import com.pulseguard.patterns.observer.AlertPublisher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Concurrently "ticks" a fleet of pumps on a fixed schedule, simulating
 * real-time monitoring. Each pump gets its own rate limiter so a single
 * misbehaving pump can't flood the alert pipeline.
 */
public class MonitoringScheduler {

    private final ScheduledExecutorService executor;
    private final Map<String, TokenBucketRateLimiter> limiters = new ConcurrentHashMap<>();
    private final AlertPublisher publisher;

    public MonitoringScheduler(AlertPublisher publisher, int threadPoolSize) {
        this.publisher = publisher;
        this.executor = Executors.newScheduledThreadPool(threadPoolSize);
    }

    public void monitor(List<InfusionPump> pumps, long periodMillis) {
        for (InfusionPump pump : pumps) {
            limiters.putIfAbsent(pump.getId(), new TokenBucketRateLimiter(5, 1));
            executor.scheduleAtFixedRate(() -> tick(pump), 0, periodMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void tick(InfusionPump pump) {
        if (pump.getState() != PumpState.INFUSING) {
            return;
        }
        TokenBucketRateLimiter limiter = limiters.get(pump.getId());
        if (limiter != null && !limiter.tryAcquire()) {
            return; // suppressed to avoid alarm-fatigue flooding
        }
        // Real hardware would report sensor deltas here; this is a monitoring
        // heartbeat hook for downstream engines to attach checks to.
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
