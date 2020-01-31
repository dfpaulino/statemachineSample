package com.example.statemachine2.subflow;

import com.example.statemachine2.dto.spcm.Plan;
import com.example.statemachine2.dto.spcm.PlanDefinition;
import java.util.ArrayList;
import java.util.List;


public class DataFlowObject {
    private String msisdn;
    private String subscriberType;
    private List<Plan> plansName=new ArrayList<>();
    private int resultCode;
    private List<PlanDefinition> allowedPlans=new ArrayList<>();
    private int nextPage =0;
    private int selectedIdx=0;
    private boolean activateRecurringPlan=false;
    private boolean deferredPlanActivation=false;
    private String deferredActivationDate;



    private boolean sharedQuota=false;

    public int getSelectedIdx() {
        return selectedIdx;
    }

    public DataFlowObject setSelectedIdx(int selectedIdx) {
        this.selectedIdx = selectedIdx;
        return this;
    }

    public int getNextPage() {
        return nextPage;
    }

    public DataFlowObject setNextPage(int nextPage) {
        this.nextPage = nextPage;
        return this;
    }

    public DataFlowObject setAllowedPlans(List<PlanDefinition> allowedPlans) {
        this.allowedPlans = allowedPlans;
        return this;
    }

    public List<PlanDefinition> getAllowedPlans() {
        return allowedPlans;
    }

    public List<Plan> getPlansName() {
        return plansName;
    }

    public DataFlowObject setPlansName(List<Plan> plansName) {
        this.plansName = plansName;
        return this;
    }


    public String getSubscriberType() {
        return subscriberType;
    }

    public DataFlowObject setSubscriberType(String subscriberType) {
        this.subscriberType = subscriberType;
        return this;
    }



    public String getMsisdn() {
        return msisdn;
    }

    public DataFlowObject setMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public int getResultCode() {
        return resultCode;
    }

    public DataFlowObject setResultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public boolean isSharedQuota() {
        return sharedQuota;
    }

    public DataFlowObject setSharedQuota(boolean sharedQuota) {
        this.sharedQuota = sharedQuota;
        return this;
    }

    public boolean isActivateRecurringPlan() {
        return activateRecurringPlan;
    }

    public DataFlowObject setActivateRecurringPlan(boolean activateRecurringPlan) {
        this.activateRecurringPlan = activateRecurringPlan;
        return this;
    }

    public boolean isDeferredPlanActivation() {
        return deferredPlanActivation;
    }

    public DataFlowObject setDeferredPlanActivation(boolean deferredPlanActivation) {
        this.deferredPlanActivation = deferredPlanActivation;
        return this;
    }

    public String getDeferredActivationDate() {
        return deferredActivationDate;
    }

    public DataFlowObject setDeferredActivationDate(String deferredActivationDate) {
        this.deferredActivationDate = deferredActivationDate;
        return this;
    }
}
