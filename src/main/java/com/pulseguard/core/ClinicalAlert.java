package com.pulseguard.core;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ClinicalAlert implements Comparable<ClinicalAlert> {

    public enum Severity {
        LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);

        private final int rank;
        Severity(int rank) { this.rank = rank; }
        public int getRank() { return rank; }
    }

    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    private final long id;
    private final String patientId;
    private final Severity severity;
    private final String message;
    private final Instant timestamp;

    public ClinicalAlert(String patientId, Severity severity, String message) {
        this.id = SEQUENCE.incrementAndGet();
        this.patientId = Objects.requireNonNull(patientId);
        this.severity = Objects.requireNonNull(severity);
        this.message = Objects.requireNonNull(message);
        this.timestamp = Instant.now();
    }

    public long getId() { return id; }
    public String getPatientId() { return patientId; }
    public Severity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }

    /**
     * Higher severity sorts first; ties broken by earlier timestamp (FIFO within
     * the same severity). This ordering is what PriorityAlertQueue relies on.
     */
    @Override
    public int compareTo(ClinicalAlert other) {
        int severityCompare = Integer.compare(other.severity.getRank(), this.severity.getRank());
        if (severityCompare != 0) return severityCompare;
        return this.timestamp.compareTo(other.timestamp);
    }

    @Override
    public String toString() {
        return String.format("[%s] Patient %s: %s", severity, patientId, message);
    }
}
