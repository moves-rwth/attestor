package de.rwth.i2.attestor.grammar.inclusion;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.HeapInclusionStrategy;

public class NormalFormInclusionStrategy implements HeapInclusionStrategy {

    @Override
    public boolean subsumes(HeapConfiguration left, HeapConfiguration right) {

        return left != null && left.equals(right);
    }
}
