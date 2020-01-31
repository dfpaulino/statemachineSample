package com.example.statemachine2.presistance;

import java.util.concurrent.TimeUnit;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import com.example.statemachine2.StateMachineConfig.States;
import com.example.statemachine2.StateMachineConfig.Events;

@Configuration
public class StateMachinePresistConfig {
    @Bean
    public CustomStateMachinePresist<States, Events, String> inMemoryPersist() {
        //return new MyStateMachinePresist();
        return new GenericStateMachineCache<States, Events, String>(getCache());
    }

    @Bean
    public StateMachinePersister<States, Events, String> persister(
            StateMachinePersist<States, Events, String> defaultPersist) {

        return new DefaultStateMachinePersister<>(defaultPersist);
    }

    public Cache getCache() {
        Cache<String,StateMachineContext<States, Events>> cache =new Cache2kBuilder<String,StateMachineContext<States, Events>>(){}
                .name("stateMachineCache")
                .eternal(false)
                .entryCapacity(10000)
                .expireAfterWrite(20,TimeUnit.SECONDS) //TODO capacity and session time should be configured
                .enableJmx(true)
                .build();

        return cache;
    }

}
