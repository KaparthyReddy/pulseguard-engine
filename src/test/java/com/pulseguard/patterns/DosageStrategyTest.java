package com.pulseguard.patterns;

import com.pulseguard.core.Medication;
import com.pulseguard.core.Patient;
import com.pulseguard.patterns.strategy.BSABasedStrategy;
import com.pulseguard.patterns.strategy.WeightBasedStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DosageStrategyTest {

    @Test
    void weightBasedCalculatesCorrectly() {
        WeightBasedStrategy strategy = new WeightBasedStrategy();
        Patient patient = new Patient("P1", "Test", 70.0, 170.0);
        Medication med = new Medication("M1", "TestDrug", "TestClass", 5.0);

        double dose = strategy.calculateDoseMg(patient, med, 0.5); // 0.5 mg/kg
        assertEquals(35.0, dose, 0.001);
    }

    @Test
    void bsaBasedCalculatesCorrectly() {
        BSABasedStrategy strategy = new BSABasedStrategy();
        Patient patient = new Patient("P1", "Test", 70.0, 170.0);
        Medication med = new Medication("M1", "TestDrug", "TestClass", 5.0);

        double expectedBsa = Math.sqrt((170.0 * 70.0) / 3600.0);
        double dose = strategy.calculateDoseMg(patient, med, 100.0); // 100 mg/m^2

        assertEquals(expectedBsa * 100.0, dose, 0.001);
    }

    @Test
    void nonPositiveTargetThrows() {
        WeightBasedStrategy strategy = new WeightBasedStrategy();
        Patient patient = new Patient("P1", "Test", 70.0, 170.0);
        Medication med = new Medication("M1", "TestDrug", "TestClass", 5.0);

        assertThrows(IllegalArgumentException.class, () -> strategy.calculateDoseMg(patient, med, 0));
    }
}
