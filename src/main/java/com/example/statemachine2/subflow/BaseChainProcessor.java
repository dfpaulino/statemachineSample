package com.example.statemachine2.subflow;

public abstract class BaseChainProcessor {
    private BaseChainProcessor next;

    public abstract boolean handleRequest(DataFlowObject data) ;

    public BaseChainProcessor successor(BaseChainProcessor next) {
        this.next=next;
        return next;
    }
    private boolean nextBlock(DataFlowObject data) {
        if (next == null) {
            return true;
        }else {
            return this.next.handleRequest(data);
        }
    }
}
