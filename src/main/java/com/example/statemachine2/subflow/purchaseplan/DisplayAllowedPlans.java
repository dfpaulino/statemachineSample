package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.subflow.DataFlowObject;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class DisplayAllowedPlans implements Action<StateMachineConfig.States, StateMachineConfig.Events> {

    private static Logger LOGGER = LoggerFactory.getLogger(DisplayAllowedPlans.class);

    private int pageSize=1;

    @Override
    public void execute(StateContext<StateMachineConfig.States, StateMachineConfig.Events> stateContext) {
        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data;
        data=(DataFlowObject)variables.get("dataObj");
        StringBuilder sb=new StringBuilder();
        sb.append("Allowed Plan\n");
        //page rollover

        if(Math.round(data.getAllowedPlans().size()/pageSize)<(data.getNextPage()+1)) {
            data.setNextPage(0);
        }

        int startIdx=data.getNextPage()*pageSize;
        //is it last page
        int maxIdx=Math.round(data.getAllowedPlans().size()/pageSize)>(data.getNextPage()+1)?startIdx+pageSize:data.getAllowedPlans().size();
        for (int i=startIdx; i<maxIdx;i++) {
            sb.append(i+" "+data.getAllowedPlans().get(i).getName()+"\n");
        }

        sb.append("* - next page\n");
        variables.put("displayMenu",sb.toString());
        LOGGER.debug("Display Allowed Plans {} page [{}]",sb.toString(),data.getNextPage()-1);
    }
}
