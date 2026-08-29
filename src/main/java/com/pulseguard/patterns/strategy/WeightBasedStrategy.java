package com.pulseguard.patterns.strategy;

import com.pulseguard.core.Medication;
import com.pulseguard.core.Patient;

/** Dose = target mg/kg * patient weight in kg. */
public class WeightBasedStrategy implements DosageCalculationStrategy {

    @Override
    public double calculateDoseMg(Patient patient, Medication medication, double targetMgPerKg) {
        if (targetMgPerKg <= 0) {
            throw new IllegalArgumentException("Target mg/kg must be positive");
        }
        return targetMgPerKg * patient.getWeightKg();
    }

    @Override
    public String getName() { return "Weight-Based Dosing"; }
}
