package com.example.statemachine2.subflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericAction1 extends BaseChainProcessor {

    Logger LOGGER = LoggerFactory.getLogger(GenericAction1.class);

    @Override
    public boolean handleRequest(DataFlowObject data) {

        LOGGER.info("GenericAction1 doing something for msisdn..." + data.getMsisdn()+"Last resultCode was "+data.getResultCode());
        return true;
    }
}
