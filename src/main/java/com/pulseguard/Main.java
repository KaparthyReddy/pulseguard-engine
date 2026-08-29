package com.pulseguard;

import com.pulseguard.core.*;
import com.pulseguard.datastructures.DrugInteractionGraph;
import com.pulseguard.engine.*;
import com.pulseguard.patterns.factory.ClinicalAlertFactory;
import com.pulseguard.patterns.observer.AlertPublisher;
import com.pulseguard.patterns.strategy.DosageCalculationStrategy;
import com.pulseguard.patterns.strategy.WeightBasedStrategy;
import com.pulseguard.security.AuditLogger;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo runner: wires up the engine end-to-end and walks through a few
 * realistic scenarios (safe dose, interaction risk, over-limit dose,
 * occlusion) so the whole pipeline can be seen firing in one run.
 */
public class Main {

    public static void main(String[] args) {
        AuditLogger auditLogger = new AuditLogger(100);
        AlertPublisher publisher = new AlertPublisher();
        AlertTriageEngine triageEngine = new AlertTriageEngine();
        publisher.subscribe(triageEngine);

        // --- Reference data ---
        Medication warfarin = new Medication("WARFARIN", "Warfarin", "Anticoagulant", 5.0);
        Medication aspirin = new Medication("ASPIRIN", "Aspirin", "NSAID", 10.0);
        Medication morphine = new Medication("MORPHINE", "Morphine", "Opioid", 2.0);

        Map<String, Medication> registry = new HashMap<>();
        registry.put(warfarin.getId(), warfarin);
        registry.put(aspirin.getId(), aspirin);
        registry.put(morphine.getId(), morphine);

        DrugInteractionGraph graph;
        try {
            graph = new InteractionGraphLoader().loadFromResource("seed-drug-interactions.json");
            auditLogger.log("SYSTEM", "GRAPH_LOAD", graph.drugCount() + " drugs loaded from seed data");
        } catch (RuntimeException e) {
            System.out.println("Could not load seed data (" + e.getMessage() + "); using empty graph.");
            graph = new DrugInteractionGraph();
        }

        DosageSafetyValidator validator = new DosageSafetyValidator();
        validator.registerLimit("WARFARIN", 10.0, 50.0);
        validator.registerLimit("ASPIRIN", 300.0, 100.0);
        validator.registerLimit("MORPHINE", 15.0, 20.0);

        InteractionChecker interactionChecker = new InteractionChecker(graph);
        PumpStateMachine stateMachine = new PumpStateMachine();
        DosageCalculationStrategy weightBased = new WeightBasedStrategy();

        // --- Patient already on Warfarin ---
        Patient patient = new Patient("P-001", "J. Rao", 68.0, 172.0);
        patient.addActiveMedication("WARFARIN");

        InfusionPump pump = new InfusionPump("PUMP-1", patient);
        pump.addObserver(triageEngine);

        System.out.println("=== Scenario 1: Interaction check (Aspirin onto a Warfarin patient) ===");
        interactionChecker.checkNewMedication(patient, aspirin, registry).forEach(publisher::publish);

        System.out.println("\n=== Scenario 2: Weight-based dose calculation + safety validation ===");
        double doseMg = weightBased.calculateDoseMg(patient, morphine, 0.3); // 0.3 mg/kg
        Dose dose = new Dose(morphine, doseMg, 5.0, Instant.now());
        System.out.printf("Calculated dose via %s: %.2fmg%n", weightBased.getName(), doseMg);
        validator.validate(patient.getId(), dose).forEach(publisher::publish);

        System.out.println("\n=== Scenario 3: Over-limit dose gets flagged ===");
        Dose unsafeDose = new Dose(morphine, 40.0, 25.0, Instant.now());
        validator.validate(patient.getId(), unsafeDose).forEach(publisher::publish);

        System.out.println("\n=== Scenario 4: Pump lifecycle + occlusion alert ===");
        stateMachine.transition(pump, PumpState.PRIMING);
        stateMachine.transition(pump, PumpState.INFUSING);
        pump.loadDose(dose);
        auditLogger.log(patient.getId(), "DOSE_LOADED", dose.toString());
        stateMachine.transition(pump, PumpState.OCCLUDED);

        System.out.println("\n=== Triage queue drained in priority order ===");
        List<ClinicalAlert> triaged = triageEngine.triageAll();
        for (ClinicalAlert alert : triaged) {
            auditLogger.log(alert.getPatientId(), "ALERT_TRIAGED", alert.toString());
        }

        System.out.println("\nSimulation complete. " + triaged.size() + " alerts processed.");
    }
}
