package com.pulseguard.engine;

import com.pulseguard.core.InfusionPump;
import com.pulseguard.core.Patient;
import com.pulseguard.core.PumpState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PumpStateMachineTest {

    @Test
    void legalTransitionSucceeds() {
        PumpStateMachine machine = new PumpStateMachine();
        InfusionPump pump = new InfusionPump("PUMP-1", new Patient("P1", "Test", 70, 170));

        machine.transition(pump, PumpState.PRIMING);
        assertEquals(PumpState.PRIMING, pump.getState());

        machine.transition(pump, PumpState.INFUSING);
        assertEquals(PumpState.INFUSING, pump.getState());
    }

    @Test
    void illegalTransitionThrows() {
        PumpStateMachine machine = new PumpStateMachine();
        InfusionPump pump = new InfusionPump("PUMP-1", new Patient("P1", "Test", 70, 170));

        assertThrows(IllegalStateException.class, () -> machine.transition(pump, PumpState.COMPLETED));
    }

    @Test
    void terminalStatesHaveNoOutgoingTransitions() {
        PumpStateMachine machine = new PumpStateMachine();
        assertFalse(machine.canTransition(PumpState.COMPLETED, PumpState.IDLE));
        assertFalse(machine.canTransition(PumpState.STOPPED, PumpState.INFUSING));
    }
}
