package com.sunkvant.config;

import com.sunkvant.statemachine.action.BeginCheckAction;
import com.sunkvant.statemachine.action.CompleteInitialWorkPermitAction;
import com.sunkvant.statemachine.action.ErrorAction;
import com.sunkvant.statemachine.action.FinishSecurityCheckAction;
import com.sunkvant.statemachine.api.model.EmployeeEvent;
import com.sunkvant.statemachine.api.model.EmployeeState;
import com.sunkvant.statemachine.listener.StateMachineApplicationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
@EnableStateMachineFactory
@EnableJpaRepositories({"org.springframework.statemachine.data.jpa", "com.sunkvant.repository"})
@EntityScan({"org.springframework.statemachine.data.jpa", "com.sunkvant.entity"})
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<EmployeeState, EmployeeEvent> {

    @Autowired
    private JpaPersistingStateMachineInterceptor<EmployeeState, EmployeeEvent, String> persister;

    @Override
    public void configure(final StateMachineConfigurationConfigurer<EmployeeState, EmployeeEvent> config) throws Exception {
        config.withPersistence()
                .runtimePersister(persister);
        config.withConfiguration()
                .autoStartup(true)
                .listener(new StateMachineApplicationListener());
    }

    @Override
    public void configure(final StateMachineStateConfigurer<EmployeeState, EmployeeEvent> states) throws Exception {
        states
                .withStates()
                .initial(EmployeeState.ADDED)
                .fork(EmployeeState.IN_CHECK)
                .join(EmployeeState.JOIN)
                .state(EmployeeState.APPROVED)
                .end(EmployeeState.ACTIVE)
                .and()
                .withStates()
                    .parent(EmployeeState.IN_CHECK)
                    .initial(EmployeeState.SECURITY_CHECK_STARTED)
                    .end(EmployeeState.SECURITY_CHECK_FINISHED)
                .and()
                .withStates()
                    .parent(EmployeeState.IN_CHECK)
                    .initial(EmployeeState.WORK_PERMIT_CHECK_STARTED)
                    .state(EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                    .end(EmployeeState.WORK_PERMIT_CHECK_FINISHED);

    }

    @Override
    public void configure(final StateMachineTransitionConfigurer<EmployeeState, EmployeeEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(EmployeeState.ADDED)
                    .target(EmployeeState.IN_CHECK)
                    .event(EmployeeEvent.BEGIN_CHECK)
                    .action(beginCheckAction(), errorAction())
                .and()
                    .withExternal()
                    .source(EmployeeState.SECURITY_CHECK_STARTED)
                    .target(EmployeeState.SECURITY_CHECK_FINISHED)
                    .event(EmployeeEvent.FINISH_SECURITY_CHECK)
                    .action(finishSecurityCheckAction(), errorAction())
                .and()
                    .withExternal()
                    .source(EmployeeState.WORK_PERMIT_CHECK_STARTED)
                    .target(EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                    .event(EmployeeEvent.COMPLETE_INITIAL_WORK_PERMIT_CHECK)
                    .action(completeInitialWorkPermitAction(), errorAction())
                .and()
                    .withExternal()
                    .source(EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                    .target(EmployeeState.WORK_PERMIT_CHECK_FINISHED)
                    .event(EmployeeEvent.FINISH_WORK_PERMIT_CHECK)
                .and()
                    .withFork()
                    .source(EmployeeState.IN_CHECK)
                    .target(EmployeeState.SECURITY_CHECK_STARTED)
                    .target(EmployeeState.WORK_PERMIT_CHECK_STARTED)
                .and()
                    .withJoin()
                    .source(EmployeeState.SECURITY_CHECK_FINISHED)
                    .source(EmployeeState.WORK_PERMIT_CHECK_FINISHED)
                    .target(EmployeeState.JOIN)
                .and()
                    .withExternal()
                    .source(EmployeeState.JOIN)
                    .target(EmployeeState.APPROVED)
                .and()
                    .withExternal()
                    .source(EmployeeState.APPROVED)
                    .target(EmployeeState.ACTIVE)
                    .event(EmployeeEvent.ACTIVATE);

    }

    @Bean
    public Action<EmployeeState, EmployeeEvent> completeInitialWorkPermitAction() {
        return new CompleteInitialWorkPermitAction();
    }

    @Bean
    public Action<EmployeeState, EmployeeEvent> beginCheckAction() {
        return new BeginCheckAction();
    }

    @Bean
    public Action<EmployeeState, EmployeeEvent> finishSecurityCheckAction() {
        return new FinishSecurityCheckAction();
    }

    @Bean
    public Action<EmployeeState, EmployeeEvent> errorAction() {
        return new ErrorAction();
    }

    @Bean
    public StateMachineService<EmployeeState, EmployeeEvent> stateMachineService(
            final StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory,
            final StateMachinePersist<EmployeeState, EmployeeEvent, String> stateMachinePersist) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachinePersist);
    }

    @Bean
    public JpaPersistingStateMachineInterceptor<EmployeeState, EmployeeEvent, String>
    jpaPersistingStateMachineInterceptor(final JpaStateMachineRepository stateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(stateMachineRepository);
    }

}
