package com.pulseguard.engine;

import com.pulseguard.core.ClinicalAlert;
import com.pulseguard.core.Medication;
import com.pulseguard.core.Patient;
import com.pulseguard.datastructures.DrugInteractionGraph;
import com.pulseguard.patterns.factory.ClinicalAlertFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Cross-checks a newly proposed medication against a patient's active
 * medications (via the interaction graph) and documented allergies.
 */
public class InteractionChecker {

    private final DrugInteractionGraph graph;

    public InteractionChecker(DrugInteractionGraph graph) {
        this.graph = graph;
    }

    public List<ClinicalAlert> checkNewMedication(Patient patient, Medication newMedication,
                                                   Map<String, Medication> medicationRegistry) {
        List<ClinicalAlert> alerts = new ArrayList<>();

        if (patient.isAllergicTo(newMedication.getName()) || patient.isAllergicTo(newMedication.getDrugClass())) {
            alerts.add(ClinicalAlertFactory.allergyAlert(patient.getId(), newMedication.getName()));
        }

        for (String activeId : patient.getActiveMedicationIds()) {
            if (activeId.equals(newMedication.getId())) continue;

            graph.getInteraction(newMedication.getId(), activeId).ifPresent(edge -> {
                Medication activeMed = medicationRegistry.get(activeId);
                String activeName = activeMed != null ? activeMed.getName() : activeId;
                if (edge.severity == DrugInteractionGraph.InteractionSeverity.MAJOR
                        || edge.severity == DrugInteractionGraph.InteractionSeverity.CONTRAINDICATED) {
                    alerts.add(ClinicalAlertFactory.interactionAlert(
                            patient.getId(), newMedication.getName(), activeName, edge.note));
                }
            });
        }
        return alerts;
    }
}
