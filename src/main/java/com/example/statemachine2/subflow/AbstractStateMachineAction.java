package com.example.statemachine2.subflow;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * This class implements {@link Action }
 * <p>A configured {@link Consumer} will be executed When the {@link org.springframework.statemachine.StateMachine} executes the action
 * by calling the method execute().</p>
 * <p>
 *     If any Exception occurs, an error state will be set on tht Statemachine for post analysis (Interceptors )
 *     Use the {@link org.springframework.statemachine.StateMachine}.hasStateMachineError()
 * </p>
 *
 *
 *
 * @param <S> Type of State
 * @param <U> Type of Event
 */
public class AbstractStateMachineAction<S,U> implements Action<S,U> {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractStateMachineAction.class);

    private Consumer<StateContext<S,U>> consumer;

    /**
     * Set the {@link Consumer} to be executed
     * @param consumer
     * @return
     */
    public AbstractStateMachineAction<S, U> setConsumer(Consumer<StateContext<S, U>> consumer) {
        this.consumer = consumer;
        return this;
    }

    @Override
    public void execute(StateContext<S, U> context) {
        LOGGER.info("Executing Action");
        try{
            this.consumer.accept(context);
        }catch (Exception e) {
            context.getStateMachine().setStateMachineError(e);
        }
    }
}
