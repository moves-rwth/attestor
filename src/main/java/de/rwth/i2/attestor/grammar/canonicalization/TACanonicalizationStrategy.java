package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;

public class TACanonicalizationStrategy extends GeneralCanonicalizationStrategy {
    public TACanonicalizationStrategy(Grammar grammar, CanonicalizationHelper canonicalizationHelper) {
        super(grammar, canonicalizationHelper);
    }

    @Override
    public HeapConfiguration canonicalize(HeapConfiguration heapConfiguration) {
        if (!(heapConfiguration instanceof TAHeapConfiguration)) {
            throw new IllegalStateException("only transformation-aware heap configurations can be handled");
        }

        TAHeapConfiguration blank = ((TAHeapConfiguration) heapConfiguration).getBlankCopy();
        return super.canonicalize(blank);
    }
}
