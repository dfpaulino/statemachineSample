package com.example.statemachine2;

import com.example.statemachine2.StateMachineConfig.States;
import com.example.statemachine2.StateMachineConfig.Events;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;

public class StateMachineErrorInterceptor extends StateMachineInterceptorAdapter<States,Events> {
    private static Logger LOGGER = LoggerFactory.getLogger(StateMachineErrorInterceptor.class);
    @Override
    public Exception stateMachineError(StateMachine<States, Events> stateMachine, Exception exception) {
        //super.stateMachineError(stateMachine,exception);
        //stateMachine.getStateMachineAccessor().doWithAllRegions();
        Map<Object, Object> variables=stateMachine.getExtendedState().getVariables();
        variables.put("final",true);
        LOGGER.error("SM [{}] Exception [{}]",stateMachine.getUuid(),exception);
        return null;
    }
}
