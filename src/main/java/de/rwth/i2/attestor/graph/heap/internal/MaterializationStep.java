package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.list.array.TIntArrayList;

public class MaterializationStep extends AbstractTransformationStep {
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
