package com.pulseguard.datastructures;

import com.pulseguard.core.ClinicalAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Custom binary heap-based priority queue for clinical alerts (built by hand,
 * not java.util.PriorityQueue, to demonstrate the underlying data structure).
 * Ordering: CRITICAL > HIGH > MEDIUM > LOW, ties broken by earliest timestamp.
 * insert/poll are O(log n); peek is O(1).
 */
public class PriorityAlertQueue {
    private final List<ClinicalAlert> heap = new ArrayList<>();

    public synchronized void insert(ClinicalAlert alert) {
        heap.add(alert);
        siftUp(heap.size() - 1);
    }

    public synchronized ClinicalAlert poll() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Alert queue is empty");
        }
        ClinicalAlert top = heap.get(0);
        ClinicalAlert last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }
        return top;
    }

    public synchronized ClinicalAlert peek() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Alert queue is empty");
        }
        return heap.get(0);
    }

    public synchronized boolean isEmpty() { return heap.isEmpty(); }
    public synchronized int size() { return heap.size(); }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(index).compareTo(heap.get(parent)) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        int size = heap.size();
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && heap.get(left).compareTo(heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && heap.get(right).compareTo(heap.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == index) break;

            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        ClinicalAlert temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}
