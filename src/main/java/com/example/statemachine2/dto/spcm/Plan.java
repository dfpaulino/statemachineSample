package com.example.statemachine2.dto.spcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Plan {
    private int id;
    @JsonProperty("planDefinition")
    private PlanDefinition planDefinition;
    private long usage;
    private String allowedUnitAmount;
    private String expiryTimestamp;


}
