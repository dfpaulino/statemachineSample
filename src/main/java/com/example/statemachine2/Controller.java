package com.example.statemachine2;

import com.example.statemachine2.presistance.CustomStateMachinePresist;
import com.example.statemachine2.subflow.DataFlowObject;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import com.example.statemachine2.StateMachineConfig.States;
import com.example.statemachine2.StateMachineConfig.Events;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "sm")
public class Controller {

    private static Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    @Autowired
    CommonsPool2TargetSource poolTargetSource;

    @Autowired
    private StateMachinePersister<States, Events, String> stateMachinePersister;
    @Autowired
    private CustomStateMachinePresist myStateMachinePresist;

    @RequestMapping(value = "/{user}/trigger",method = RequestMethod.GET)
    public String trigger(@PathVariable(value="user") String user, @RequestParam(value = "response",required = false) String rsp ) throws  Exception {
        boolean smHasError=false;

        StateMachine<StateMachineConfig.States, StateMachineConfig.Events>  stateMachine = getStateMachine();
        try {
            resetSmFromStore(user,stateMachine);
            sendEventToSm(user,stateMachine ,Events.INIT);
            LOGGER.info("logic2 stuff >>"+stateMachine.getExtendedState().getVariables().getOrDefault("logic2","no logic2 stuff"));
            smHasError=(stateMachine.hasStateMachineError())?true:false;

        }catch (Exception e){
            LOGGER.error("Exception",e);
        }finally {
            poolTargetSource.releaseTarget(stateMachine);
        }

        if(!smHasError){
            return "{\"menu\":\""+stateMachine.getExtendedState().getVariables().getOrDefault("displayMenu","Shit no menu")+"\"}";
        }else {
            return "{\"menu\":\""+"System Error"+"\"}";
        }

    }

    @RequestMapping(value = "/{user}/subsequent/trigger",method = RequestMethod.GET)
    public String triggerSubSeq(@PathVariable(value="user") String user, @RequestParam(value = "response") String rsp ) throws Exception {
        StateMachine<StateMachineConfig.States, StateMachineConfig.Events>  stateMachine=getStateMachine();;

            try{
                resetSmFromStore(user,stateMachine);
                Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
                variables.put("userResponse",rsp);
                DataFlowObject data=(DataFlowObject)variables.get("dataObj");
                sendEventToSm(data.getMsisdn(), stateMachine,Events.USERRSP);
                LOGGER.info("logic2 stuff >>"+stateMachine.getExtendedState().getVariables().getOrDefault("logic2","no logic2 stuff"));

                return "{\"menu\":\""+stateMachine.getExtendedState().getVariables().getOrDefault("displayMenu","Shit no menu")+"\"}";
            }catch (Exception e){
                LOGGER.error("Cant send Event to SM exception []",e);
            }finally {
                    poolTargetSource.releaseTarget(stateMachine);
            }

            return "shite";
    }


    private void sendEventToSm(@NotNull String user, StateMachine<States, Events> stateMachine, StateMachineConfig.Events event) throws Exception{
        Map<Object, Object> variables = stateMachine.getExtendedState().getVariables();
        DataFlowObject data;
        LOGGER.info("Before State:{}",stateMachine.getState());
        if(variables.containsKey("dataObj")) {

            data=(DataFlowObject)variables.get("dataObj");
            LOGGER.info("Found dataObj in context for user {}",data.getMsisdn());
        }else {
            LOGGER.info("Creating {}",user);
            data=new DataFlowObject().setMsisdn(user);
            variables.put("dataObj",data);
        }
        boolean result=stateMachine.sendEvent(event);
        LOGGER.info("After State:{}",stateMachine.getState());
        boolean isFinal=(boolean)variables.getOrDefault("final",false);
        boolean smHasError=stateMachine.hasStateMachineError();
        if(isFinal||smHasError ) {
            myStateMachinePresist.remove(user);
            System.out.println("user removed");
        }else {
            LOGGER.info("storing statemachine id{}",stateMachine.getUuid());
            stateMachinePersister.persist(stateMachine, user);
        }
    }

    private StateMachine<States, Events> getStateMachine() throws Exception {
        StateMachine<StateMachineConfig.States, StateMachineConfig.Events>  stateMachine =
                (StateMachine<States, Events> )poolTargetSource.getTarget();
        LOGGER.info("getting statemachine id{} hasError[{}]",stateMachine.getUuid(),stateMachine.hasStateMachineError());
        return stateMachine;
    }

    private  StateMachine<States, Events> resetSmFromStore(String user,StateMachine<States, Events> stateMachine)  throws Exception{

        return stateMachinePersister.restore(stateMachine,user);
    }
}
