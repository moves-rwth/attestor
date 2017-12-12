package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategy;

public class FakeIndexCanonicalizationStrategy implements IndexCanonizationStrategy {

    @Override
    public void canonizeIndex(HeapConfiguration hc) {
        // do nothing
    }

}
