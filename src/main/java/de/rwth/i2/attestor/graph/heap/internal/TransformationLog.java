package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import gnu.trove.list.array.TIntArrayList;

public abstract class TransformationLog {
    public final int ntEdge;
    public final HeapConfiguration rule;

    public abstract int match(int id);

    public TransformationLog(int ntEdge, HeapConfiguration rule) {
        this.ntEdge = ntEdge;
        this.rule = rule;
    }
}

class MaterializationLog extends TransformationLog {
    private final TIntArrayList matching;

    public MaterializationLog(int ntEdge, HeapConfiguration rule, TIntArrayList matching) {
        super(ntEdge, rule);
        this.matching = matching;
    }

    @Override
    public int match(int id) {
        return this.matching.get(id);
    }
}

class CanonicalizationLog extends TransformationLog {
    private final Matching matching;

    public CanonicalizationLog(int ntEdge, HeapConfiguration rule, Matching matching) {
        super(ntEdge, rule);
        this.matching = matching;
    }

    @Override
    public int match(int id) {
        return this.matching.match(id);
    }
}
