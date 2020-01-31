package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.subflow.DataFlowObject;
import java.util.Map;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class GetSubscriberAllowedPlansAction implements Action<StateMachineConfig.States, StateMachineConfig.Events> {
    private static Logger LOGGER = LoggerFactory.getLogger(GetSubscriberAllowedPlansAction.class);

    GetSubscriberAllowedPlans getSubscriberAllowedPlans;

    public GetSubscriberAllowedPlansAction(CloseableHttpClient hc) {
        this.getSubscriberAllowedPlans = new GetSubscriberAllowedPlans(hc);
    }

    @Override
    public void execute(StateContext<StateMachineConfig.States, StateMachineConfig.Events> stateContext) {
        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data=(DataFlowObject)  variables.get("dataObj");
        data.setSharedQuota((boolean)variables.getOrDefault("shareQuota",false));
        variables.put("final",getSubscriberAllowedPlans.handleRequest(data)==true?false:true);
        LOGGER.info("getSubscriberAllowedPlansAction >>>Getting Allowed plan");
    }
}
