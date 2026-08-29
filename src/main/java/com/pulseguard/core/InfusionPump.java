package com.pulseguard.core;

import com.pulseguard.patterns.observer.PumpObserver;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class InfusionPump {
    private final String id;
    private final Patient patient;
    private volatile PumpState state = PumpState.IDLE;
    private volatile Dose currentDose;
    private final List<PumpObserver> observers = new CopyOnWriteArrayList<>();

    public InfusionPump(String id, Patient patient) {
        this.id = Objects.requireNonNull(id);
        this.patient = Objects.requireNonNull(patient);
    }

    public String getId() { return id; }
    public Patient getPatient() { return patient; }
    public PumpState getState() { return state; }
    public Dose getCurrentDose() { return currentDose; }

    public void addObserver(PumpObserver observer) { observers.add(observer); }
    public void removeObserver(PumpObserver observer) { observers.remove(observer); }

    public synchronized void setState(PumpState newState) {
        PumpState old = this.state;
        this.state = newState;
        for (PumpObserver observer : observers) {
            observer.onStateChange(this, old, newState);
        }
    }

    public synchronized void loadDose(Dose dose) {
        this.currentDose = Objects.requireNonNull(dose);
    }

    @Override
    public String toString() {
        return String.format("Pump[%s, state=%s, patient=%s]", id, state, patient.getId());
    }
}
