package com.sunkvant.statemachine.action;

import com.sunkvant.statemachine.api.model.EmployeeEvent;
import com.sunkvant.statemachine.api.model.EmployeeState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

@Slf4j
public class ErrorAction implements Action<EmployeeState, EmployeeEvent> {
    @Override
    public void execute(final StateContext<EmployeeState, EmployeeEvent> context) {
        System.out.println("Error change status " + context.getTarget().getId());
    }
}
