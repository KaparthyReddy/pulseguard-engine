package com.pulseguard.patterns.strategy;

import com.pulseguard.core.Medication;
import com.pulseguard.core.Patient;

/**
 * Dose = target mg/m^2 * patient body surface area (Mosteller formula).
 * Commonly used for chemotherapeutic and high-potency agents.
 */
public class BSABasedStrategy implements DosageCalculationStrategy {

    @Override
    public double calculateDoseMg(Patient patient, Medication medication, double targetMgPerM2) {
        if (targetMgPerM2 <= 0) {
            throw new IllegalArgumentException("Target mg/m^2 must be positive");
        }
        return targetMgPerM2 * patient.getBodySurfaceAreaM2();
    }

    @Override
    public String getName() { return "Body-Surface-Area Dosing"; }
}
