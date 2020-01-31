package com.example.statemachine2.subflow;

/**
 * Chain of Responsibility
 * Multiple executions in 1 transition
 */

public class Flow {

    private BaseChainProcessor flow;

    public Flow() {
        flow=new GetSubscriberProfileStage().successor(new GenericAction1());
    }
    public boolean execute(DataFlowObject data){
        return this.flow.handleRequest(data);
    }
}
