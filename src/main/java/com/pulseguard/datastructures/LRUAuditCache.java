package com.pulseguard.datastructures;

import java.util.HashMap;
import java.util.Map;

/**
 * Fixed-capacity LRU cache for recently touched audit records, O(1) get/put.
 * Backed by a HashMap + hand-rolled doubly linked list (no java.util.LinkedHashMap).
 */
public class LRUAuditCache<K, V> {

    private class Node {
        K key;
        V value;
        Node prev, next;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<K, Node> map = new HashMap<>();
    private final Node head; // most-recently-used sentinel
    private final Node tail; // least-recently-used sentinel

    public LRUAuditCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        head = new Node(null, null);
        tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    public synchronized V get(K key) {
        Node node = map.get(key);
        if (node == null) return null;
        moveToFront(node);
        return node.value;
    }

    public synchronized void put(K key, V value) {
        Node existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }
        Node node = new Node(key, value);
        map.put(key, node);
        addToFront(node);

        if (map.size() > capacity) {
            Node lru = tail.prev;
            removeNode(lru);
            map.remove(lru.key);
        }
    }

    public synchronized int size() { return map.size(); }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }
}
