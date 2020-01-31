package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.dto.spcm.PlanDefinition;
import com.example.statemachine2.subflow.DataFlowObject;
import com.example.statemachine2.StateMachineConfig.Events;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

@NoArgsConstructor
public class CheckDisplayPlanInfoOpt implements Action<StateMachineConfig.States, StateMachineConfig.Events> {
    private static Logger LOGGER = LoggerFactory.getLogger(CheckDisplayPlanInfoOpt.class);



    @Override
    public void execute(StateContext<StateMachineConfig.States, StateMachineConfig.Events> stateContext) {

        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data;
        if (variables.containsKey("userResponse")) {
            String response = (String) variables.get("userResponse");
            data = (DataFlowObject) variables.get("dataObj");
            LOGGER.info("User Response <<" + response);
            PlanDefinition plan = data.getAllowedPlans().get(data.getSelectedIdx());
            Events nextEvt;
            if (plan.isRecurring()) {
                switch (response) {
                    case "1":
                        data.setActivateRecurringPlan(true);
                        nextEvt = plan.isDeferredActivationAllowed() ? Events.CHK_DEFERRED_EVT : Events.PURCHASEPLAN;
                        break;
                    case "2":
                        data.setActivateRecurringPlan(false);
                        nextEvt = plan.isDeferredActivationAllowed() ? Events.CHK_DEFERRED_EVT : Events.PURCHASEPLAN;
                        break;
                    case "3":
                        nextEvt = Events.GOBACK;
                        break;
                    default:
                        nextEvt = Events.GOBACK;
                }

            } else {
                switch (response) {
                    case "1":
                        nextEvt = plan.isDeferredActivationAllowed() ? Events.CHK_DEFERRED_EVT : Events.PURCHASEPLAN;
                        break;
                    case "2":
                        nextEvt = Events.GOBACK;
                        break;
                    default:
                        nextEvt = Events.GOBACK;
                }
            }
            stateContext.getStateMachine().sendEvent(nextEvt);

        } else {
            //TODO exception is a better choice...
            variables.put("displayMenu", "Error getting menu...");
            LOGGER.warn("DisplayMenu2 No response present");
        }

    }
}
