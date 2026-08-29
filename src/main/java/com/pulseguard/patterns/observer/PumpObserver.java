package com.pulseguard.patterns.observer;

import com.pulseguard.core.ClinicalAlert;
import com.pulseguard.core.InfusionPump;
import com.pulseguard.core.PumpState;

public interface PumpObserver {
    void onStateChange(InfusionPump pump, PumpState oldState, PumpState newState);

    void onAlert(ClinicalAlert alert);
}
