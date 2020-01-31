package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.StateMachineConfig;
import com.example.statemachine2.StateMachineConfig.Events;
import com.example.statemachine2.subflow.DataFlowObject;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import com.example.statemachine2.util.DateUtil;

@NoArgsConstructor
public class CheckPlanDeferredDate implements Action<StateMachineConfig.States, Events> {
    private static Logger LOGGER = LoggerFactory.getLogger(CheckPlanDeferredDate.class);



    @Override
    public void execute(StateContext<StateMachineConfig.States, Events> stateContext) {

        Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
        DataFlowObject data;
        if (variables.containsKey("userResponse")) {
            String response = (String) variables.get("userResponse");
            data = (DataFlowObject) variables.get("dataObj");
            LOGGER.info("User Response <<" + response);
            Events nextEvt;
            if(DateUtil.isValid("ddmmyyyyhh",response)) {
                nextEvt=Events.PURCHASEPLAN;
                try {


                Date auxDate=DateUtil.stringToDate(response,"ddmmyyyyhh");
                String spcmDate=DateUtil.dateToString(auxDate,"YYYY-MM-DDTHH");
                data.setDeferredActivationDate(spcmDate);
                }catch (ParseException pe){
                    LOGGER.warn("Error converting Date");
                    throw new RuntimeException("Error converting Date");
                }
                stateContext.getStateMachine().sendEvent(nextEvt);
            }else {
                variables.put("displayMenu", "Wrong Date Format...");
                LOGGER.warn("Display: Wrong date format");
                throw new RuntimeException("Wrong Date Format");
            }



        } else {
            //TODO exception is a better choice...
            variables.put("displayMenu", "Error getting menu...");
            LOGGER.warn("DisplayMenu2 No response present");
        }

    }
}
