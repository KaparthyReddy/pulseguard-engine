package com.pulseguard.engine;

import com.pulseguard.core.ClinicalAlert;
import com.pulseguard.core.InfusionPump;
import com.pulseguard.core.PumpState;
import com.pulseguard.datastructures.PriorityAlertQueue;
import com.pulseguard.patterns.factory.ClinicalAlertFactory;
import com.pulseguard.patterns.observer.PumpObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Subscribes to pump/alert events, funnels everything through the priority
 * queue, and exposes triage-ordered draining for downstream handling
 * (paging, UI, audit logging, etc.).
 */
public class AlertTriageEngine implements PumpObserver {

    private final PriorityAlertQueue queue = new PriorityAlertQueue();

    @Override
    public void onStateChange(InfusionPump pump, PumpState oldState, PumpState newState) {
        if (newState == PumpState.OCCLUDED) {
            queue.insert(ClinicalAlertFactory.occlusionAlert(pump.getPatient().getId()));
        } else if (newState == PumpState.ALARM) {
            queue.insert(ClinicalAlertFactory.lowPriorityInfo(pump.getPatient().getId(),
                    "Pump " + pump.getId() + " entered ALARM state"));
        }
    }

    @Override
    public void onAlert(ClinicalAlert alert) {
        queue.insert(alert);
    }

    public boolean hasPending() { return !queue.isEmpty(); }

    public ClinicalAlert triageNext() { return queue.poll(); }

    /** Drains the whole queue in priority order — highest severity first. */
    public List<ClinicalAlert> triageAll() {
        List<ClinicalAlert> ordered = new ArrayList<>();
        while (!queue.isEmpty()) {
            ordered.add(queue.poll());
        }
        return ordered;
    }

    public int pendingCount() { return queue.size(); }
}
