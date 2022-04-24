package com.sunkvant.statemachine.interceptor;

import com.sunkvant.entity.Employee;
import com.sunkvant.repository.EmployeeRepository;
import com.sunkvant.statemachine.api.model.EmployeeEvent;
import com.sunkvant.statemachine.api.model.EmployeeState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateMachineInterceptor extends StateMachineInterceptorAdapter<EmployeeState, EmployeeEvent> {

    private final EmployeeRepository employeeRepository;

    @Override
    public void postStateChange(State<EmployeeState, EmployeeEvent> state, Message<EmployeeEvent> message,
            Transition<EmployeeState, EmployeeEvent> transition,
            StateMachine<EmployeeState, EmployeeEvent> stateMachine,
            StateMachine<EmployeeState, EmployeeEvent> rootStateMachine) {

        Employee employee = employeeRepository.findById(Long.valueOf(rootStateMachine.getId())).orElse(null);

        if (employee == null) {
            log.error("Employee is not found");
            return;
        }

        if (state.getId().equals(EmployeeState.ACTIVE)) {
            employee.setState(EmployeeState.ACTIVE.getValue());
            stateMachine.stop();
        } else {
            String employeeState = rootStateMachine.getState().getIds()
                    .stream().map(EmployeeState::getValue).collect(Collectors.joining(", "));
            employee.setState(employeeState);
        }

        employeeRepository.save(employee);
    }

    @Override
    public Exception stateMachineError(StateMachine<EmployeeState, EmployeeEvent> stateMachine, Exception exception) {
        log.error("{} StateMachine encountered error: [ message: {}]", "stateMachineError()", exception.getMessage());
        return super.stateMachineError(stateMachine, exception);
    }

}
