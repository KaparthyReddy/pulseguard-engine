package com.pulseguard.datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LRUAuditCacheTest {

    @Test
    void evictsLeastRecentlyUsedWhenFull() {
        LRUAuditCache<String, String> cache = new LRUAuditCache<>(2);
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3"); // should evict "a"

        assertNull(cache.get("a"));
        assertEquals("2", cache.get("b"));
        assertEquals("3", cache.get("c"));
    }

    @Test
    void getRefreshesRecency() {
        LRUAuditCache<String, String> cache = new LRUAuditCache<>(2);
        cache.put("a", "1");
        cache.put("b", "2");
        cache.get("a");       // "a" is now most recently used
        cache.put("c", "3");  // should evict "b", not "a"

        assertEquals("1", cache.get("a"));
        assertNull(cache.get("b"));
    }

    @Test
    void updatingExistingKeyDoesNotGrowSize() {
        LRUAuditCache<String, String> cache = new LRUAuditCache<>(2);
        cache.put("a", "1");
        cache.put("a", "2");
        assertEquals(1, cache.size());
        assertEquals("2", cache.get("a"));
    }
}
