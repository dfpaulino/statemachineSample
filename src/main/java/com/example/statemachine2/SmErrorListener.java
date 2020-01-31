package com.example.statemachine2;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

public class SmErrorListener extends StateMachineListenerAdapter<StateMachineConfig.States, StateMachineConfig.Events> {
    private static Logger LOGGER = LoggerFactory.getLogger(SmErrorListener.class);

    @Override
    public void stateMachineError(StateMachine<StateMachineConfig.States, StateMachineConfig.Events> stateMachine, Exception exception) {
        LOGGER.error("Exception Occurred: Stop Flow E {}",exception.getMessage());
        Map<Object, Object> variables=stateMachine.getExtendedState().getVariables();
        variables.put("final",true);
    }

    @Override
    public void eventNotAccepted(Message<StateMachineConfig.Events> event) {
        LOGGER.error("Inside eventNotAccepted");
    }
}
