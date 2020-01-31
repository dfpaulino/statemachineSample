package com.example.statemachine2.dto.spcm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlanDefinition {
    private int id;
    private String name;
    private int cost;
    private String unitAmount;
    private String unitMeteringType;
    private String validityPeriod;
    private String classification;
    private boolean recurring;
    private boolean core;
    private boolean shared;
    private boolean deferredActivationAllowed;
    private boolean shareQuota;
    private long grantedAmount;
    private String summary;
    private String description;
}
