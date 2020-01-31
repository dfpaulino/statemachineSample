package com.example.statemachine2.presistance;

import org.springframework.statemachine.StateMachinePersist;

public interface CustomStateMachinePresist<S, E, T> extends StateMachinePersist<S, E, T> {

    void remove(T contextObj);
}
