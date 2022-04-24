package com.sunkvant.service;

import com.sunkvant.service.TransitionService;
import com.sunkvant.service.impl.TransitionServiceImpl;
import com.sunkvant.statemachine.api.model.EmployeeDtoOut;
import com.sunkvant.statemachine.api.model.EmployeeEvent;
import com.sunkvant.statemachine.api.model.EmployeeState;
import com.sunkvant.statemachine.constant.ContextVariable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
public class TransitionServiceImplTest {

    @Mock
    private StateMachineService<EmployeeState, EmployeeEvent> stateMachineService;

    @Mock
    private StateMachineInterceptor<EmployeeState, EmployeeEvent> stateMachineInterceptor;

    @Autowired
    private TransitionService transitionService;

    @InjectMocks
    private TransitionServiceImpl transitionServiceWithMocks;

    @Test
    void transition_Fail() {
        var employee = new EmployeeDtoOut();
        employee.setId(100L);

        boolean accepted = transitionService.transition(employee, EmployeeEvent.ACTIVATE);

        assertFalse(accepted);
    }

    @Test
    void transition() {
        var employee = new EmployeeDtoOut();
        employee.setId(100L);

        var stateMachine = mock(StateMachine.class, Mockito.RETURNS_DEEP_STUBS);

        ArgumentCaptor<Long> argumentCaptorEmployeeId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EmployeeEvent> argumentCaptorEvent = ArgumentCaptor.forClass(EmployeeEvent.class);

        when(stateMachineService.acquireStateMachine(eq(employee.getId().toString()))).thenReturn(stateMachine);
        when(stateMachine.getExtendedState().getVariables().put(eq(ContextVariable.EMPLOYEE_ID.getName()), argumentCaptorEmployeeId.capture())).thenReturn(null);
        when(stateMachine.sendEvent(argumentCaptorEvent.capture())).thenReturn(false);

        transitionServiceWithMocks.transition(employee, EmployeeEvent.BEGIN_CHECK);

        assertEquals(100L, argumentCaptorEmployeeId.getValue());
        assertEquals(EmployeeEvent.BEGIN_CHECK, argumentCaptorEvent.getValue());
    }

}
