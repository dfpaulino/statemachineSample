package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.StateMachineConfig.Events;
import com.example.statemachine2.dto.spcm.PlanDefinition;
import com.example.statemachine2.subflow.DataFlowObject;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

@NoArgsConstructor
public class CheckPlanDeferredAction implements Action<StateMachineConfig.States, Events> {
    private static Logger LOGGER = LoggerFactory.getLogger(CheckPlanDeferredAction.class);



    @Override
    public void execute(StateContext<StateMachineConfig.States, Events> stateContext) {

        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data;
        if (variables.containsKey("userResponse")) {
            String response = (String) variables.get("userResponse");
            data = (DataFlowObject) variables.get("dataObj");
            LOGGER.info("User Response <<" + response);
            Events nextEvt;
                switch (response) {
                    case "1":
                        data.setDeferredPlanActivation(false);
                        nextEvt = Events.PURCHASEPLAN;
                        break;
                    case "2":
                        data.setDeferredPlanActivation(true);
                        nextEvt =Events.GET_DEFERRED_DATE;
                        break;
                    default:
                        nextEvt = Events.GOBACK;
                }

            stateContext.getStateMachine().sendEvent(nextEvt);

        } else {
            //TODO exception is a better choice...
            variables.put("displayMenu", "Error getting menu...");
            LOGGER.warn("DisplayMenu2 No response present");
        }

    }
}
