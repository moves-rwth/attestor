package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.map.TIntObjectMap;

public interface IndexAbstractionRule<I> {
    TIntObjectMap<I> abstractForward(I index, HeapConfiguration rule);

    I abstractBackward(TIntObjectMap<I> assign, HeapConfiguration rule);
}
