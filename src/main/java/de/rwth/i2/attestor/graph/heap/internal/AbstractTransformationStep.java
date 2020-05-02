package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public abstract class AbstractTransformationStep implements TransformationStep {
    private final int ntEdge;
    private final HeapConfiguration rule;

    @Override
    public int getNtEdge() {
        return ntEdge;
    }

    @Override
    public HeapConfiguration getRule() {
        return rule;
    }

    public AbstractTransformationStep(int ntEdge, HeapConfiguration rule) {
        this.ntEdge = ntEdge;
        this.rule = rule;
    }
}
