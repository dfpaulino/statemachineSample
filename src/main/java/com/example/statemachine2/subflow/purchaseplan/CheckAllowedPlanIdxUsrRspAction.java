package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.subflow.DataFlowObject;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class CheckAllowedPlanIdxUsrRspAction implements Action<StateMachineConfig.States, StateMachineConfig.Events> {
    private static Logger LOGGER = LoggerFactory.getLogger(CheckAllowedPlanIdxUsrRspAction.class);

    @Override
    public void execute(StateContext<StateMachineConfig.States, StateMachineConfig.Events> stateContext) {

        int chosenIdx;
        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data;
        if(variables.containsKey("userResponse")) {
            String response =(String) variables.get("userResponse");
            data=(DataFlowObject)variables.get("dataObj");
            LOGGER.info("CheckAllowedPlanIdxUsrRspAction User Response <<"+response);
            if(response.equals("*")) {
                data.setNextPage(data.getNextPage()+1);
                LOGGER.info("CheckAllowedPlanIdxUsrRspAction Settign nextPage {}",data.getNextPage());
                stateContext.getStateMachine().sendEvent(StateMachineConfig.Events.NEXTIDX);
            }
            else{
                chosenIdx=Integer.valueOf(response);
                if(chosenIdx<=(data.getAllowedPlans().size()-1)) {
                    LOGGER.info("CheckAllowedPlanIdxUsrRspAction get planIdx {}",chosenIdx);
                    data.setSelectedIdx(chosenIdx);
                    stateContext.getStateMachine().sendEvent(StateMachineConfig.Events.DISPLAY_PLAN_INFO_EVT);
                }else {
                    data.setNextPage(data.getNextPage()+1);
                    stateContext.getStateMachine().sendEvent(StateMachineConfig.Events.NEXTIDX);
                    LOGGER.info("CheckAllowedPlanIdxUsrRspAction Invalid planIdx {}",chosenIdx);
                }
            }
        }else {
            variables.put("displayMenu","no response...");
            LOGGER.info("DisplayMenu2 No response present");
        }

    }
}
