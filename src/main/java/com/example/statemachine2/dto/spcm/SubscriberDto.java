package com.example.statemachine2.dto.spcm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriberDto {
    private String msisdn;
    private String imsi;
    private String paymentType;
    private String locale;
    private String status;
}
