package com.example.statemachine2.presistance;

import com.example.statemachine2.StateMachineConfig;
import java.util.HashMap;
import org.springframework.statemachine.StateMachineContext;

public class MyStateMachinePresist implements CustomStateMachinePresist<StateMachineConfig.States, StateMachineConfig.Events, String> {

    private HashMap<String, StateMachineContext<StateMachineConfig.States, StateMachineConfig.Events>> storage
            = new HashMap<>();

    @Override
    public void write(StateMachineContext<StateMachineConfig.States, StateMachineConfig.Events> stateMachineContext, String contextObj) {
        System.out.println("MyStateMachinePresist: storing context for key >"+contextObj);
        storage.put(contextObj, stateMachineContext);
    }

    @Override
    public StateMachineContext<StateMachineConfig.States, StateMachineConfig.Events> read(String contextObj)  {
        System.out.println("MyStateMachinePresist: Reading context for key >"+contextObj);
        return storage.get(contextObj);
    }

    @Override
    public void remove(String contextObj) {
        storage.remove(contextObj);
        System.out.println("MyStateMachinePresist: Removing context for key >"+contextObj);
    }
}
