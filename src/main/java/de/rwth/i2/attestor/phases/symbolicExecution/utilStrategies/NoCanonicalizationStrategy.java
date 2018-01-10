package de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class NoCanonicalizationStrategy implements CanonicalizationStrategy {

    @Override
    public HeapConfiguration canonicalize(HeapConfiguration heapConfiguration) {

        return heapConfiguration;
    }
}
