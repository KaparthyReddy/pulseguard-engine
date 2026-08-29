package com.pulseguard.engine;

import com.pulseguard.core.ClinicalAlert;
import com.pulseguard.core.Dose;
import com.pulseguard.patterns.factory.ClinicalAlertFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validates a proposed dose against per-medication safe limits before it's
 * ever loaded onto a pump. Limits are registered per medication id.
 */
public class DosageSafetyValidator {

    private static class Limit {
        final double maxDoseMg;
        final double maxRateMlPerHr;
        Limit(double maxDoseMg, double maxRateMlPerHr) {
            this.maxDoseMg = maxDoseMg;
            this.maxRateMlPerHr = maxRateMlPerHr;
        }
    }

    private final Map<String, Limit> limits = new HashMap<>();

    public void registerLimit(String medicationId, double maxDoseMg, double maxRateMlPerHr) {
        limits.put(medicationId, new Limit(maxDoseMg, maxRateMlPerHr));
    }

    /**
     * @return list of violations found (empty means the dose is within registered limits)
     */
    public List<ClinicalAlert> validate(String patientId, Dose dose) {
        List<ClinicalAlert> violations = new ArrayList<>();
        Limit limit = limits.get(dose.getMedication().getId());
        if (limit == null) {
            // No registered limit means we can't clear it as safe — flag for review.
            violations.add(ClinicalAlertFactory.lowPriorityInfo(patientId,
                    "No safety limit registered for " + dose.getMedication().getName() + " — manual review required"));
            return violations;
        }

        if (dose.getAmountMg() > limit.maxDoseMg) {
            violations.add(ClinicalAlertFactory.dosageAlert(patientId, dose.getAmountMg(), limit.maxDoseMg));
        }
        if (dose.getRateMlPerHr() > limit.maxRateMlPerHr) {
            violations.add(ClinicalAlertFactory.rateAlert(patientId, dose.getRateMlPerHr(), limit.maxRateMlPerHr));
        }
        return violations;
    }
}
