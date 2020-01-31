package com.example.statemachine2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.monitor.AbstractStateMachineMonitor;
import org.springframework.statemachine.transition.Transition;

public class TestStateMachineMonitor extends AbstractStateMachineMonitor<StateMachineConfig.States, StateMachineConfig.Events> {
    private static Logger LOGGER = LoggerFactory.getLogger(TestStateMachineMonitor.class);
    @Override
    public void transition(StateMachine<StateMachineConfig.States, StateMachineConfig.Events> stateMachine, Transition<StateMachineConfig.States, StateMachineConfig.Events> transition, long duration) {
        if(transition.getSource()!=null) {
            LOGGER.info("From transition {} to transtistion{} duration {}", transition.getSource().getId(), transition.getTarget().getId(), duration);

        }
    }

    @Override
    public void action(StateMachine<StateMachineConfig.States, StateMachineConfig.Events> stateMachine, Action<StateMachineConfig.States, StateMachineConfig.Events> action, long duration) {
        LOGGER.info("Action {} duration {}",action.getClass(),duration);
    }
}
