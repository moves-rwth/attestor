package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import gnu.trove.list.array.TIntArrayList;

public abstract class TransformationStep {
    public final int ntEdge;
    public final HeapConfiguration rule;

    public abstract int match(int id);

    public TransformationStep(int ntEdge, HeapConfiguration rule) {
        this.ntEdge = ntEdge;
        this.rule = rule;
    }
}

class MaterializationStep extends TransformationStep {
    private final TIntArrayList matching;

    public MaterializationStep(int ntEdge, HeapConfiguration rule, TIntArrayList matching) {
        super(ntEdge, rule);
        this.matching = matching;
    }

    @Override
    public int match(int id) {
        return this.matching.get(id);
    }
}

class CanonicalizationStep extends TransformationStep {
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
