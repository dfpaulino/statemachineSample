package com.example.statemachine2.dto.spcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriberAllowedPlans {
    private long length;
    @JsonProperty("planDefinition")
    private List<PlanDefinition> plans;

}
