package com.example.statemachine2;

import com.example.statemachine2.subflow.AbstractStateMachineAction;
import com.example.statemachine2.StateMachineConfig.States;
import com.example.statemachine2.StateMachineConfig.Events;
import com.example.statemachine2.subflow.BaseChainProcessor;
import com.example.statemachine2.subflow.DataFlowObject;
import com.example.statemachine2.subflow.GenericAction1;
import com.example.statemachine2.subflow.GetSubscriberProfileStage;
import java.util.Map;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;

public class GetSubscriberInfo1Action extends AbstractStateMachineAction<States,Events> {

    private static Logger LOGGER = LoggerFactory.getLogger(GetSubscriberInfo1Action.class);

    BaseChainProcessor processor;

    public GetSubscriberInfo1Action(CloseableHttpClient hc) {
        processor=new GetSubscriberProfileStage();
        processor.successor(new GenericAction1());
        this.setConsumer((c)->{this.consume(c);});
    }

    private void consume(StateContext<States,Events> c) {
        LOGGER.info("doLogic1 >>>Doing some logic");
        Map<Object, Object> variables = c.getExtendedState().getVariables();
        DataFlowObject data=(DataFlowObject)  variables.get("dataObj");
        processor.handleRequest(data);
    }

}
