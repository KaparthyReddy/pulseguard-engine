package com.pulseguard.patterns.strategy;

import com.pulseguard.core.Medication;
import com.pulseguard.core.Patient;

public interface DosageCalculationStrategy {
    /** @return recommended dose in milligrams */
    double calculateDoseMg(Patient patient, Medication medication, double targetDoseParameter);

    String getName();
}
