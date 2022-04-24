package com.sunkvant.service.impl;

import com.sunkvant.service.TransitionService;
import com.sunkvant.statemachine.api.model.EmployeeDtoOut;
import com.sunkvant.statemachine.api.model.EmployeeEvent;
import com.sunkvant.statemachine.api.model.EmployeeState;
import com.sunkvant.statemachine.constant.ContextVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitionServiceImpl implements TransitionService {
    private final StateMachineService<EmployeeState, EmployeeEvent> stateMachineService;
    private final StateMachineInterceptor<EmployeeState, EmployeeEvent> stateMachineInterceptor;

    @Override
    public boolean transition(EmployeeDtoOut employee, EmployeeEvent event) {
        var stateMachine = stateMachineService.acquireStateMachine(employee.getId().toString());
        stateMachine.getExtendedState().getVariables().put(ContextVariable.EMPLOYEE_ID.getName(), employee.getId());
        stateMachine.getStateMachineAccessor().doWithAllRegions(sm -> sm.addStateMachineInterceptor(stateMachineInterceptor));
        return stateMachine.sendEvent(event);
    }
}
