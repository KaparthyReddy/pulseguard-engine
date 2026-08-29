package com.pulseguard.engine;

import com.pulseguard.core.ClinicalAlert;
import com.pulseguard.core.Dose;
import com.pulseguard.core.Medication;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DosageSafetyValidatorTest {

    @Test
    void doseWithinLimitsProducesNoViolations() {
        DosageSafetyValidator validator = new DosageSafetyValidator();
        Medication med = new Medication("M1", "TestDrug", "TestClass", 5.0);
        validator.registerLimit("M1", 100.0, 50.0);

        Dose dose = new Dose(med, 50.0, 20.0, Instant.now());
        assertTrue(validator.validate("P1", dose).isEmpty());
    }

    @Test
    void overLimitDoseAndRateBothFlagged() {
        DosageSafetyValidator validator = new DosageSafetyValidator();
        Medication med = new Medication("M1", "TestDrug", "TestClass", 5.0);
        validator.registerLimit("M1", 100.0, 50.0);

        Dose dose = new Dose(med, 150.0, 80.0, Instant.now());
        List<ClinicalAlert> violations = validator.validate("P1", dose);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().allMatch(v -> v.getSeverity() == ClinicalAlert.Severity.CRITICAL
                || v.getSeverity() == ClinicalAlert.Severity.HIGH));
    }

    @Test
    void unregisteredMedicationFlaggedForReview() {
        DosageSafetyValidator validator = new DosageSafetyValidator();
        Medication med = new Medication("UNKNOWN", "Mystery", "Unknown", 1.0);
        Dose dose = new Dose(med, 10.0, 10.0, Instant.now());

        List<ClinicalAlert> violations = validator.validate("P1", dose);
        assertEquals(1, violations.size());
        assertEquals(ClinicalAlert.Severity.LOW, violations.get(0).getSeverity());
    }
}
