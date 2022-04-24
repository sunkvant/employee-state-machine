package com.sunkvant.statemachine.action;

import com.sunkvant.statemachine.constant.ContextVariable;
import com.sunkvant.statemachine.api.model.EmployeeEvent;
import com.sunkvant.statemachine.api.model.EmployeeState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

@Slf4j
public class CompleteInitialWorkPermitAction implements Action<EmployeeState, EmployeeEvent> {
    @Override
    public void execute(StateContext<EmployeeState, EmployeeEvent> stateContext) {
        final Long employeeId = stateContext.getExtendedState().get(ContextVariable.EMPLOYEE_ID.getName(), Long.class);
        log.info(String.format("Pending verification employee: [id=%s]", employeeId));
    }
}
