package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.dto.spcm.PlanDefinition;
import com.example.statemachine2.subflow.DataFlowObject;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class DisplayGetDeferredActivationDateDate implements Action<StateMachineConfig.States, StateMachineConfig.Events> {

    private static Logger LOGGER = LoggerFactory.getLogger(DisplayGetDeferredActivationDateDate.class);

    @Override
    public void execute(StateContext<StateMachineConfig.States, StateMachineConfig.Events> stateContext) {
        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data;
        data=(DataFlowObject)variables.get("dataObj");
        StringBuilder sb=new StringBuilder();
        PlanDefinition plan=data.getAllowedPlans().get(data.getSelectedIdx());
        sb.append(plan.getName()).append("\n");
        sb.append("Please enter the activation Date in the format ddmmyyyyhh\n * - go Back ");

        variables.put("displayMenu",sb.toString());
        LOGGER.debug("Display info [{}]",sb.toString());
    }
}
