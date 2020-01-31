package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.subflow.DataFlowObject;
import java.util.Map;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class PurchasePlanAction implements Action<StateMachineConfig.States, StateMachineConfig.Events> {

    private static Logger LOGGER = LoggerFactory.getLogger(PurchasePlanAction.class);

    private SpcmPurchasePlan spcmPurchasePlan;

    public PurchasePlanAction(CloseableHttpClient hc) {
        this.spcmPurchasePlan = new SpcmPurchasePlan(hc);
    }


    @Override
    public void execute(StateContext<StateMachineConfig.States, StateMachineConfig.Events> stateContext) {
        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data=(DataFlowObject)  variables.get("dataObj");
        spcmPurchasePlan.handleRequest(data);
        LOGGER.info("purchasePlanAction >>>Getting  plan");
        variables.put("final",true);
        if(data.getResultCode()==0) {
            variables.put("displayMenu","purchase of plan success");
        }else{
            variables.put("displayMenu","purchase of plan Failed");
        }

    }
}
