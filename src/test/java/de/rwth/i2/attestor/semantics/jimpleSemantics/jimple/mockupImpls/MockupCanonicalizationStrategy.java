package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class MockupCanonicalizationStrategy implements CanonicalizationStrategy {

    @Override
    public HeapConfiguration canonicalize(HeapConfiguration heapConfiguration) {

        return heapConfiguration;
    }

}
