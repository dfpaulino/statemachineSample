package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.dto.spcm.PlanDefinition;
import com.example.statemachine2.subflow.DataFlowObject;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class DisplayDeferedPlanMenu implements Action<StateMachineConfig.States, StateMachineConfig.Events> {

    private static Logger LOGGER = LoggerFactory.getLogger(DisplayDeferedPlanMenu.class);
    private static final String AUTO_RENEW_OPTIONS="1 - activate with Auto renew,2- activate with out Auto renew\n3-cancel";
    private static final String NO_AUTO_RENEW_OPTIONS="1 - activate \n2-cancel";

    @Override
    public void execute(StateContext<StateMachineConfig.States, StateMachineConfig.Events> stateContext) {
        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data;
        data=(DataFlowObject)variables.get("dataObj");
        StringBuilder sb=new StringBuilder();
        PlanDefinition plan=data.getAllowedPlans().get(data.getSelectedIdx());
        sb.append(plan.getName()).append("\n");
        sb.append("1 - Activate now\n2- Activate later");

        variables.put("displayMenu",sb.toString());
        LOGGER.debug("Display plan menu info [{}]",sb.toString());
    }
}
