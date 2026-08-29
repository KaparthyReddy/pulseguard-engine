package com.pulseguard.core;

import java.time.Instant;
import java.util.Objects;

public class Dose {
    private final Medication medication;
    private final double amountMg;
    private final double rateMlPerHr;
    private final Instant startTime;

    public Dose(Medication medication, double amountMg, double rateMlPerHr, Instant startTime) {
        if (amountMg <= 0 || rateMlPerHr <= 0) {
            throw new IllegalArgumentException("Dose amount and rate must be positive");
        }
        this.medication = Objects.requireNonNull(medication);
        this.amountMg = amountMg;
        this.rateMlPerHr = rateMlPerHr;
        this.startTime = Objects.requireNonNull(startTime);
    }

    public Medication getMedication() { return medication; }
    public double getAmountMg() { return amountMg; }
    public double getRateMlPerHr() { return rateMlPerHr; }
    public Instant getStartTime() { return startTime; }

    public double getVolumeMl() {
        return amountMg / medication.getConcentrationMgPerMl();
    }

    public double getEstimatedDurationHours() {
        return getVolumeMl() / rateMlPerHr;
    }

    @Override
    public String toString() {
        return String.format("Dose[%s, %.2fmg @ %.2fmL/hr]", medication.getName(), amountMg, rateMlPerHr);
    }
}
