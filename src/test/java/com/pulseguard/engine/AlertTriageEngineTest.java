package com.pulseguard.engine;

import com.pulseguard.core.ClinicalAlert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertTriageEngineTest {

    @Test
    void onAlertQueuesForTriage() {
        AlertTriageEngine engine = new AlertTriageEngine();
        engine.onAlert(new ClinicalAlert("P1", ClinicalAlert.Severity.HIGH, "test alert"));

        assertTrue(engine.hasPending());
        assertEquals(1, engine.pendingCount());
    }

    @Test
    void triageAllDrainsInSeverityOrder() {
        AlertTriageEngine engine = new AlertTriageEngine();
        engine.onAlert(new ClinicalAlert("P1", ClinicalAlert.Severity.LOW, "low"));
        engine.onAlert(new ClinicalAlert("P1", ClinicalAlert.Severity.CRITICAL, "critical"));

        List<ClinicalAlert> ordered = engine.triageAll();
        assertEquals(ClinicalAlert.Severity.CRITICAL, ordered.get(0).getSeverity());
        assertEquals(ClinicalAlert.Severity.LOW, ordered.get(1).getSeverity());
        assertFalse(engine.hasPending());
    }
}
