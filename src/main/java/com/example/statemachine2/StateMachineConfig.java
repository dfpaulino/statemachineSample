package com.example.statemachine2;

import com.example.statemachine2.subflow.DataFlowObject;
import com.example.statemachine2.subflow.Flow;
import com.example.statemachine2.subflow.purchaseplan.CheckAllowedPlanIdxUsrRspAction;
import com.example.statemachine2.subflow.purchaseplan.CheckDisplayPlanInfoOpt;
import com.example.statemachine2.subflow.purchaseplan.CheckPlanDeferredAction;
import com.example.statemachine2.subflow.purchaseplan.CheckPlanDeferredDate;
import com.example.statemachine2.subflow.purchaseplan.DisplayAllowedPlans;
import com.example.statemachine2.subflow.purchaseplan.DisplayDeferedPlanMenu;
import com.example.statemachine2.subflow.purchaseplan.DisplayGetDeferredActivationDateDate;
import com.example.statemachine2.subflow.purchaseplan.DisplayPlanInfoMenu;
import com.example.statemachine2.subflow.purchaseplan.GetSubscriberAllowedPlansAction;
import com.example.statemachine2.subflow.purchaseplan.PurchasePlanAction;
import com.example.statemachine2.util.http.client.HttpClientPool;
import java.util.Map;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.monitor.StateMachineMonitor;

@Configuration
@EnableStateMachine
public class StateMachineConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(StateMachineConfig.class);

    @Bean(name = "stateMachineTarget")
    @Scope(scopeName = "prototype")
    //@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public StateMachine<States, Events> stateMachine1() throws Exception {
        LOGGER.info("stateMachineTarget bean Creation");
        StateMachineBuilder.Builder<States, Events> builder = StateMachineBuilder.<States, Events>builder();
        builder.configureConfiguration()
                    .withConfiguration()
                        .listener(new SmErrorListener())
                        .autoStartup(true);
        builder.configureConfiguration()
                    .withMonitoring()
                        .monitor(stateMachineMonitor());


        builder.configureStates()
                .withStates()
                    .initial(States.INIT)
                    //.states(EnumSet.allOf(States.class))
                    .state(States.SELECT_INDIV_SHARE_PLAN, displayMenu1(),null)
                    .state(States.URSP1,chkUr1(),null)
                    .state(States.SHOWALLOWDEPLAN, displayAllowedPlans(),null)
                    .state(States.USRRSPALLOEDPLAN, checkAllowedPlanIdxUsrRsp(),null)
                    .state(States.DISPLAY_PLAN_INFO,displayPlanInfoMenu(),null)
                    .state(States.USR_RSP_DISPLAY_PLAN_INFO,checkDisplayPlanInfoOpt(),null)
                    .state(States.DEFERRED_ACTIVATION_MENU, displayDeferredPlanMenu(),null)
                    //IF deferred plan
                    .state(States.USR_RSP_DEFERRED_ACTIVATION, checkPlanDeferredAction(),null)
                    .state(States.DEFERRED_ACTIVATION_GET_DATE,displayGetDeferredActivationDateDate(),null)
                    .state(States.USR_RSP_DEFERRED_GET_DATE,checkPlanDeferredDate(),null)
                    //END deferred plan
                    .state(States.PURCHASEPLAN)
                    .end(States.PURCHASEPLAN)
                    .end(States.EXIT);


        builder.configureTransitions()
                //Main Menu
                .withExternal()
                    .source(States.INIT).target(States.SELECT_INDIV_SHARE_PLAN)
                    .event(Events.INIT)
                    .action(getSubscriberInfo1Action())
                    .and()
                .withExternal()
                    .source(States.SELECT_INDIV_SHARE_PLAN).target(States.URSP1)
                    .event(Events.USERRSP)
                    .and()
                .withExternal()
                    .source(States.URSP1).target(States.SHOWALLOWDEPLAN)
                    .event(Events.GET_SPCM_PLANS_EVT)
                    .action(getSubscriberAllowedPlansAction())
                    .and()
                .withExternal()
                    .source(States.SHOWALLOWDEPLAN).target(States.USRRSPALLOEDPLAN)
                    .event(Events.USERRSP)
                    .and()
                .withExternal()
                    .source(States.USRRSPALLOEDPLAN).target(States.SHOWALLOWDEPLAN)
                    .event(Events.NEXTIDX)
                    .and()
                .withExternal()
                    .source(States.USRRSPALLOEDPLAN).target(States.DISPLAY_PLAN_INFO)
                    .event(Events.DISPLAY_PLAN_INFO_EVT)
                    .and()
                .withExternal()
                    .source(States.DISPLAY_PLAN_INFO).target(States.USR_RSP_DISPLAY_PLAN_INFO)
                    .event(Events.USERRSP)
                    .and()
                .withExternal()
                    .source(States.USR_RSP_DISPLAY_PLAN_INFO).target(States.SHOWALLOWDEPLAN)
                    .event(Events.GOBACK)
                    .and()
                //IF deferred Plan
                .withExternal()
                    .source(States.USR_RSP_DISPLAY_PLAN_INFO).target(States.DEFERRED_ACTIVATION_MENU)
                    .event(Events.CHK_DEFERRED_EVT)
                    .and()
                .withExternal()
                    .source(States.DEFERRED_ACTIVATION_MENU).target(States.USR_RSP_DEFERRED_ACTIVATION)
                    .event(Events.USERRSP)
                    .and()
                .withExternal()
                    .source(States.USR_RSP_DEFERRED_ACTIVATION).target(States.DEFERRED_ACTIVATION_GET_DATE)
                    .event(Events.GET_DEFERRED_DATE)
                    .and()
                .withExternal()
                    .source(States.USR_RSP_DEFERRED_ACTIVATION).target(States.PURCHASEPLAN)
                    .event(Events.PURCHASEPLAN)
                    .and()
                .withExternal()
                    .source(States.USR_RSP_DEFERRED_ACTIVATION).target(States.SHOWALLOWDEPLAN)
                    .event(Events.GOBACK)
                    .and()
                .withExternal()
                    .source(States.DEFERRED_ACTIVATION_GET_DATE).target(States.USR_RSP_DEFERRED_GET_DATE)
                    .event(Events.USERRSP)
                    .and()
                .withExternal()
                    .source(States.USR_RSP_DEFERRED_GET_DATE).target(States.PURCHASEPLAN)
                    .event(Events.PURCHASEPLAN)
                    .and()
                .withExternal()
                    .source(States.USR_RSP_DEFERRED_GET_DATE).target(States.SHOWALLOWDEPLAN)
                    .event(Events.GOBACK)
                    .and()
                //END deferred
                .withExternal()
                    .source(States.USR_RSP_DISPLAY_PLAN_INFO).target(States.PURCHASEPLAN)
                    .event(Events.PURCHASEPLAN)
                    .action(purchasePlanAction());

        StateMachine<States, Events> sm=builder.build();

        sm.getStateMachineAccessor().doWithAllRegions((f)->f.addStateMachineInterceptor(new StateMachineErrorInterceptor()));
        return sm;
    }


    public enum States {
        INIT,

        SELECT_INDIV_SHARE_PLAN,
        URSP1,

        SHOWALLOWDEPLAN,
        USRRSPALLOEDPLAN,
        DISPLAY_PLAN_INFO,
        USR_RSP_DISPLAY_PLAN_INFO,
        //deferred states
        DEFERRED_ACTIVATION_MENU,
        USR_RSP_DEFERRED_ACTIVATION,
        DEFERRED_ACTIVATION_GET_DATE,
        USR_RSP_DEFERRED_GET_DATE,
        //END deferred Sates
        PURCHASEPLAN,
        EXIT
    }

    public enum Events {
        //From Controller
        INIT,
        USERRSP,

        //General
        NEXTIDX,
        GOBACK,

        //user response trigger
        GET_SPCM_PLANS_EVT,
        DISPLAY_PLAN_INFO_EVT,
        //deferred events
        CHK_DEFERRED_EVT,
        GET_DEFERRED_DATE,
        //End deferred events

        PURCHASEPLAN
    }

    @Bean public Action<States,Events> displayMenu1() {
        return new DisplaySelectindividOrSharePlanMenu();
    }
    @Bean public Action<States,Events> getSubscriberInfo1Action() {return new GetSubscriberInfo1Action(getHttpPool());}
    @Bean public Action<States,Events> chkUr1() {return new CheckUrRspSelectIndivOrSharePlan();}
    @Bean public Action<States,Events> getSubscriberAllowedPlansAction()  {return new GetSubscriberAllowedPlansAction(getHttpPool());}
    @Bean public Action<States,Events> displayAllowedPlans() {return new DisplayAllowedPlans();}
    @Bean public Action<States,Events> checkAllowedPlanIdxUsrRsp() {return  new CheckAllowedPlanIdxUsrRspAction();}
    @Bean public Action<States,Events> purchasePlanAction() {return new PurchasePlanAction(getHttpPool());}
    @Bean public Action<States,Events> displayPlanInfoMenu() {return new DisplayPlanInfoMenu();}
    @Bean public Action<States,Events> checkDisplayPlanInfoOpt() {return new CheckDisplayPlanInfoOpt();}
    @Bean public Action<States,Events> displayDeferredPlanMenu() {return new DisplayDeferedPlanMenu();}
    @Bean public Action<States,Events> checkPlanDeferredAction(){ return new CheckPlanDeferredAction();}
    @Bean public Action<States,Events> checkPlanDeferredDate() {return new CheckPlanDeferredDate();}

    @Bean public Action<States,Events> displayGetDeferredActivationDateDate() {return new DisplayGetDeferredActivationDateDate();}


    @Bean public StateMachineMonitor<States, Events> stateMachineMonitor() {return new TestStateMachineMonitor();}

    public static class DisplaySelectindividOrSharePlanMenu implements Action<States,Events> {

        @Override
        public void execute(StateContext<States, Events> stateContext) {
            Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
            DataFlowObject data=(DataFlowObject)  variables.get("dataObj");

            String s=String.format("This Plan Purchase msisdn %s type %s \n 1. Individual Plan \n 2.Shareable Plan",
                    data.getMsisdn(),data.getSubscriberType());
            variables.put("displayMenu",s);
            LOGGER.info("DisplaySelectindividOrSharePlanMenu >>>"+s);

        }
    }


    public static class CheckUrRspSelectIndivOrSharePlan implements Action<States,Events> {

        @Override
        public void execute(StateContext<States, Events> stateContext) {
            Map<Object, Object> variables = stateContext.getExtendedState().getVariables();
            String response = (String) variables.get("userResponse");
            LOGGER.info("CheckUrRspSelectIndivOrSharePlan User Response <<" + response);
            //TODO should get the enum from the ordinal value
            switch (response) {
                case "1":
                    variables.put("shareQuota", false);
                    break;
                case "2":
                    variables.put("shareQuota", true);
                    break;
                default:
                    variables.put("shareQuota", false);
            }
            stateContext.getStateMachine().sendEvent(Events.GET_SPCM_PLANS_EVT);
        }

    }
    /*
    this should go on its own config class
    * */
    @Bean(name="sm")
    //@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ProxyFactoryBean stateMachinePool() {
        ProxyFactoryBean pfb = new ProxyFactoryBean();
        pfb.setTargetSource(poolTargetSource());
        LOGGER.info("ProxyFactoryBean bean Creation");
        return pfb;
    }

    @Bean
    public CommonsPool2TargetSource poolTargetSource() {
        CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
        pool.setMaxSize(2);

        pool.setTargetBeanName("stateMachineTarget");
        LOGGER.info("poolTargetSource bean Creation");
        return pool;
    }

    @Bean
    public CloseableHttpClient getHttpPool(){

        HttpClientPool httpClientPool = HttpClientPool.Builder.newInstance()
                .setMaxConnections(10)
                .setDefaultMaxPerRoute(2)
                .setMaxConnPerRoute(10)
                .build();

        return httpClientPool.getHttpClient();
    }


}
