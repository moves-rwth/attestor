package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.CollapsedHeapConfiguration;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;


public class GeneralCanonicalizationStrategy implements CanonicalizationStrategy {

    private final Grammar grammar;
    private final CanonicalizationHelper canonicalizationHelper;

    public GeneralCanonicalizationStrategy(Grammar grammar,
                                           CanonicalizationHelper canonicalizationHelper) {

        this.grammar = grammar;
        this.canonicalizationHelper = canonicalizationHelper;
    }

    @Override
    public HeapConfiguration canonicalize(HeapConfiguration heapConfiguration) {

        return performCanonicalization(heapConfiguration);
    }

    private HeapConfiguration performCanonicalization(HeapConfiguration heapConfiguration) {

        heapConfiguration = canonicalizationHelper.prepareHeapForCanonicalization(heapConfiguration);
        for (Nonterminal lhs : grammar.getAllLeftHandSides()) {
            for (HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                HeapConfiguration abstractedHeap =
                        canonicalizationHelper.tryReplaceMatching(heapConfiguration, rhs, lhs);
                if (abstractedHeap != null) {
                    return performCanonicalization(abstractedHeap);
                }
            }

            for(CollapsedHeapConfiguration rhs : grammar.getCollapsedRightHandSidesFor(lhs)) {
                        HeapConfiguration abstractedHeap =
                        canonicalizationHelper.tryReplaceMatching(heapConfiguration, rhs, lhs);
                if (abstractedHeap != null) {
                    return performCanonicalization(abstractedHeap);
                }
            }

        }
        return heapConfiguration;
    }
}
