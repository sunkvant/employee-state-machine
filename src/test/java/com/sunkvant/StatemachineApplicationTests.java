package com.sunkvant;

import com.sunkvant.repository.EmployeeRepository;
import com.sunkvant.service.TransitionService;
import com.sunkvant.statemachine.api.model.EmployeeEvent;
import com.sunkvant.statemachine.api.model.EmployeeState;
import com.sunkvant.statemachine.interceptor.StateMachineInterceptor;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestExecution;

import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
class StatemachineApplicationTests {

	@Autowired
	private StateMachineService<EmployeeState, EmployeeEvent> stateMachineService;
	private StateMachine<EmployeeState, EmployeeEvent> stateMachine;

	@BeforeEach
	void setup() {
		stateMachine = stateMachineService.acquireStateMachine(UUID.randomUUID().toString());
	}


	@Test
	void transition_ADDED_Success() throws Exception {
		StateMachineTestPlan<EmployeeState, EmployeeEvent> testPlan = StateMachineTestPlanBuilder
				.<EmployeeState, EmployeeEvent>builder().stateMachine(stateMachine).step()
				.expectState(EmployeeState.ADDED).and().build();
		testPlan.test();
	}

	@Test
	void transition_INCHECK_Success() throws Exception {
		StateMachineTestPlan<EmployeeState, EmployeeEvent> testPlan = StateMachineTestPlanBuilder
				.<EmployeeState, EmployeeEvent>builder().stateMachine(stateMachine).step()
				.sendEvent(EmployeeEvent.BEGIN_CHECK)
				.expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_STARTED, EmployeeState.WORK_PERMIT_CHECK_STARTED).
				expectStateChanged(2).and().build();
		testPlan.test();
	}

	@Test
	void transition_ACTIVATE_Success() throws Exception {
		StateMachineTestPlan<EmployeeState, EmployeeEvent> testPlan = StateMachineTestPlanBuilder
				.<EmployeeState, EmployeeEvent>builder().stateMachine(stateMachine).step()
				.sendEvent(EmployeeEvent.BEGIN_CHECK)
				.expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_STARTED, EmployeeState.WORK_PERMIT_CHECK_STARTED).
				expectStateChanged(2).and().step()
				.sendEvent(EmployeeEvent.FINISH_SECURITY_CHECK)
				.expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_FINISHED, EmployeeState.WORK_PERMIT_CHECK_STARTED)
				.expectStateChanged(1).and().step()
				.sendEvent(EmployeeEvent.COMPLETE_INITIAL_WORK_PERMIT_CHECK)
				.expectStates(EmployeeState.IN_CHECK, EmployeeState.SECURITY_CHECK_FINISHED, EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
				.expectStateChanged(1).and().step()
				.sendEvent(EmployeeEvent.FINISH_WORK_PERMIT_CHECK)
				.expectState(EmployeeState.APPROVED)
				.expectStateChanged(2).and().step()
				.sendEvent(EmployeeEvent.ACTIVATE)
				.expectState(EmployeeState.ACTIVE)
				.expectStateChanged(1).and().build();
		testPlan.test();
	}

}
