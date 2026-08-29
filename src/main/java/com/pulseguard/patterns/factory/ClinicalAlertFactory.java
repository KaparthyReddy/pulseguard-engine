package com.pulseguard.patterns.factory;

import com.pulseguard.core.ClinicalAlert;

/**
 * Centralizes alert construction so severity/wording stays consistent
 * no matter which engine component raises the alert.
 */
public final class ClinicalAlertFactory {

    private ClinicalAlertFactory() { }

    public static ClinicalAlert occlusionAlert(String patientId) {
        return new ClinicalAlert(patientId, ClinicalAlert.Severity.CRITICAL,
                "Line occlusion detected — infusion halted");
    }

    public static ClinicalAlert interactionAlert(String patientId, String drugA, String drugB, String note) {
        return new ClinicalAlert(patientId, ClinicalAlert.Severity.HIGH,
                String.format("Potential interaction between %s and %s: %s", drugA, drugB, note));
    }

    public static ClinicalAlert dosageAlert(String patientId, double requestedMg, double maxSafeMg) {
        return new ClinicalAlert(patientId, ClinicalAlert.Severity.CRITICAL,
                String.format("Requested dose %.2fmg exceeds safe maximum %.2fmg", requestedMg, maxSafeMg));
    }

    public static ClinicalAlert rateAlert(String patientId, double rateMlPerHr, double maxRateMlPerHr) {
        return new ClinicalAlert(patientId, ClinicalAlert.Severity.HIGH,
                String.format("Infusion rate %.2fmL/hr exceeds max %.2fmL/hr", rateMlPerHr, maxRateMlPerHr));
    }

    public static ClinicalAlert allergyAlert(String patientId, String medicationName) {
        return new ClinicalAlert(patientId, ClinicalAlert.Severity.CRITICAL,
                String.format("Patient has a documented allergy relevant to %s", medicationName));
    }

    public static ClinicalAlert lowPriorityInfo(String patientId, String message) {
        return new ClinicalAlert(patientId, ClinicalAlert.Severity.LOW, message);
    }
}
