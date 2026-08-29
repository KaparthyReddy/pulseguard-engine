package com.pulseguard.datastructures;

import com.pulseguard.core.ClinicalAlert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityAlertQueueTest {

    @Test
    void pollsHighestSeverityFirst() {
        PriorityAlertQueue queue = new PriorityAlertQueue();
        queue.insert(new ClinicalAlert("P1", ClinicalAlert.Severity.LOW, "low"));
        queue.insert(new ClinicalAlert("P1", ClinicalAlert.Severity.CRITICAL, "critical"));
        queue.insert(new ClinicalAlert("P1", ClinicalAlert.Severity.MEDIUM, "medium"));

        assertEquals(ClinicalAlert.Severity.CRITICAL, queue.poll().getSeverity());
        assertEquals(ClinicalAlert.Severity.MEDIUM, queue.poll().getSeverity());
        assertEquals(ClinicalAlert.Severity.LOW, queue.poll().getSeverity());
    }

    @Test
    void tiesBrokenByInsertionOrder() throws InterruptedException {
        PriorityAlertQueue queue = new PriorityAlertQueue();
        ClinicalAlert first = new ClinicalAlert("P1", ClinicalAlert.Severity.HIGH, "first");
        Thread.sleep(2);
        ClinicalAlert second = new ClinicalAlert("P1", ClinicalAlert.Severity.HIGH, "second");

        queue.insert(second);
        queue.insert(first);

        assertEquals("first", queue.poll().getMessage());
        assertEquals("second", queue.poll().getMessage());
    }

    @Test
    void pollOnEmptyQueueThrows() {
        PriorityAlertQueue queue = new PriorityAlertQueue();
        assertThrows(java.util.NoSuchElementException.class, queue::poll);
    }

    @Test
    void sizeAndIsEmptyTrackCorrectly() {
        PriorityAlertQueue queue = new PriorityAlertQueue();
        assertTrue(queue.isEmpty());
        queue.insert(new ClinicalAlert("P1", ClinicalAlert.Severity.LOW, "x"));
        assertEquals(1, queue.size());
        queue.poll();
        assertTrue(queue.isEmpty());
    }
}
