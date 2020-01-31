package com.example.statemachine2.presistance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.cache2k.Cache2kBuilder;
import org.cache2k.core.InternalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachineContext;
import org.cache2k.Cache;

public class GenericStateMachineCache<S, E, T> implements CustomStateMachinePresist<S, E, T> {


    private static Logger LOGGER = LoggerFactory.getLogger(GenericStateMachineCache.class);

    private Cache<T, StateMachineContext<S, E>> cache;
    //private Map<T, StateMachineContext<S, E>> cache;

    public GenericStateMachineCache(Cache<T, StateMachineContext<S, E>> cache) {
        this.cache = cache;
    }

    /*
    public GenericStateMachineCache(int size){

        //cache=new HashMap<>(size);
         cache =new Cache2kBuilder<T, StateMachineContext<S, E>>(){}
                .name("ss")
                .eternal(false)
                .entryCapacity(100000)
                .expireAfterWrite(20,TimeUnit.SECONDS)
                .enableJmx(true)
                .build();

    }
    */
    @Override
    public void remove(T contextObj) {
        LOGGER.info("removing entry {} in cache",contextObj.toString());
        cache.remove(contextObj);
    }

    @Override
    public void write(StateMachineContext<S, E> context, T contextObj)  {
        LOGGER.info("writing entry key {} in cache",contextObj.toString());

        cache.put(contextObj,context);
        //LOGGER.info("cache current size {}out of {}",((InternalCache)cache).getInfo().toString(), ((InternalCache)cache).getInfo().getSize());
    }

    @Override
    public StateMachineContext<S, E> read(T contextObj)  {
        LOGGER.info("reading entry {} from cache",contextObj.toString());
        return cache.get(contextObj);
    }
}
