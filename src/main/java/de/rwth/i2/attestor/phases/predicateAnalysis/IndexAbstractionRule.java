package de.rwth.i2.attestor.phases.predicateAnalysis;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Map;

public interface IndexAbstractionRule<I> {
    Map<Integer, I> abstractForward(I index, Nonterminal nt, HeapConfiguration rule);

    I abstractBackward(Map<Integer, I> assign, Nonterminal nt, HeapConfiguration rule);
}
