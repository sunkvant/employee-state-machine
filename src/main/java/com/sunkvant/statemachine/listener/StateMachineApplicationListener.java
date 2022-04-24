package com.sunkvant.statemachine.listener;

import com.sunkvant.statemachine.api.model.EmployeeEvent;
import com.sunkvant.statemachine.api.model.EmployeeState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
public class StateMachineApplicationListener extends StateMachineListenerAdapter<EmployeeState, EmployeeEvent> {
    @Override
    public void stateChanged(State<EmployeeState, EmployeeEvent> from, State<EmployeeState, EmployeeEvent> to) {
        super.stateChanged(from, to);
        log.info(String.format("State changed to: %s", to.getId().name()));
    }
}
