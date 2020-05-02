package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;

public class CanonicalizationStep extends AbstractTransformationStep {
    private final Matching matching;

    public CanonicalizationStep(int ntEdge, HeapConfiguration rule, Matching matching) {
        super(ntEdge, rule);
        this.matching = matching;
    }

    @Override
    public int match(int id) {
        return this.matching.match(id);
    }
}
