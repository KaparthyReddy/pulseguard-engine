package com.pulseguard.patterns.observer;

import com.pulseguard.core.ClinicalAlert;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central publish/subscribe hub that fans a clinical alert out to every
 * registered observer (logging, UI mock, downstream engines, etc.).
 */
public class AlertPublisher {
    private final List<PumpObserver> subscribers = new CopyOnWriteArrayList<>();

    public void subscribe(PumpObserver observer) { subscribers.add(observer); }
    public void unsubscribe(PumpObserver observer) { subscribers.remove(observer); }

    public void publish(ClinicalAlert alert) {
        for (PumpObserver observer : subscribers) {
            observer.onAlert(alert);
        }
    }

    public int subscriberCount() { return subscribers.size(); }
}
