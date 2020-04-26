package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.predicateAnalysis.relativeIndex.RelativeIndex;
import gnu.trove.map.TIntObjectMap;

public interface BackwardAbstractionRule<T extends RelativeIndex>  {
    T apply(TIntObjectMap<T> assign, HeapConfiguration grammarRule);
}
