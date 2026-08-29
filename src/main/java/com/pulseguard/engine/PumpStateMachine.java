package com.pulseguard.engine;

import com.pulseguard.core.InfusionPump;
import com.pulseguard.core.PumpState;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Enforces legal state transitions for an infusion pump so the engine can
 * never, say, jump straight from IDLE to COMPLETED or resume out of ALARM
 * without an explicit clear.
 */
public class PumpStateMachine {

    private static final Map<PumpState, Set<PumpState>> ALLOWED = new EnumMap<>(PumpState.class);

    static {
        ALLOWED.put(PumpState.IDLE, EnumSet.of(PumpState.PRIMING));
        ALLOWED.put(PumpState.PRIMING, EnumSet.of(PumpState.INFUSING, PumpState.ALARM));
        ALLOWED.put(PumpState.INFUSING, EnumSet.of(PumpState.PAUSED, PumpState.OCCLUDED,
                PumpState.ALARM, PumpState.COMPLETED));
        ALLOWED.put(PumpState.PAUSED, EnumSet.of(PumpState.INFUSING, PumpState.STOPPED));
        ALLOWED.put(PumpState.OCCLUDED, EnumSet.of(PumpState.ALARM, PumpState.PAUSED));
        ALLOWED.put(PumpState.ALARM, EnumSet.of(PumpState.PAUSED, PumpState.STOPPED));
        ALLOWED.put(PumpState.COMPLETED, EnumSet.noneOf(PumpState.class));
        ALLOWED.put(PumpState.STOPPED, EnumSet.noneOf(PumpState.class));
    }

    public boolean canTransition(PumpState from, PumpState to) {
        return ALLOWED.getOrDefault(from, EnumSet.noneOf(PumpState.class)).contains(to);
    }

    public void transition(InfusionPump pump, PumpState to) {
        PumpState from = pump.getState();
        if (!canTransition(from, to)) {
            throw new IllegalStateException(
                    String.format("Illegal pump transition: %s -> %s (pump %s)", from, to, pump.getId()));
        }
        pump.setState(to);
    }
}
