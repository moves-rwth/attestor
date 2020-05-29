package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.map.TIntObjectMap;

public interface IndexAbstractionRule<I> {
    TIntObjectMap<I> abstractForward(I index, Nonterminal nt, HeapConfiguration rule);

    I abstractBackward(TIntObjectMap<I> assign, Nonterminal nt, HeapConfiguration rule);
}
