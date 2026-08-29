package com.pulseguard.security;

import com.pulseguard.datastructures.LRUAuditCache;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Append-style audit trail for clinically significant actions (dose loaded,
 * pump state changed, alert triaged, override applied). Recent entries are
 * kept in a bounded LRU cache for fast lookup; every entry is also written
 * to the sink (stdout here, would be a durable store in production).
 */
public class AuditLogger {

    private final LRUAuditCache<Long, String> recentEntries;
    private final AtomicLong sequence = new AtomicLong(0);

    public AuditLogger(int recentCapacity) {
        this.recentEntries = new LRUAuditCache<>(recentCapacity);
    }

    public long log(String actorId, String action, String detail) {
        long entryId = sequence.incrementAndGet();
        String formatted = String.format("[%s] entry=%d actor=%s action=%s detail=%s",
                Instant.now(), entryId, actorId, action, detail);
        recentEntries.put(entryId, formatted);
        System.out.println(formatted); // stand-in sink for a durable audit store
        return entryId;
    }

    public String getRecentEntry(long entryId) {
        return recentEntries.get(entryId);
    }
}
