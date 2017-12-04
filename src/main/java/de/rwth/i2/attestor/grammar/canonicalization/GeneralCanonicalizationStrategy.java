package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;


public class GeneralCanonicalizationStrategy implements CanonicalizationStrategy {

    private final Grammar grammar;
    private final CanonicalizationHelper canonicalizationHelper;

    public GeneralCanonicalizationStrategy(Grammar grammar,
                                           CanonicalizationHelper canonicalizationHelper) {

        this.grammar = grammar;
        this.canonicalizationHelper = canonicalizationHelper;
    }

    @Override
    public ProgramState canonicalize(ProgramState state) {

        ProgramState result = performCanonicalization(state);
        return result;
    }

    private ProgramState performCanonicalization(ProgramState state) {

        state = canonicalizationHelper.prepareHeapForCanonicalization(state);

        for (Nonterminal lhs : grammar.getAllLeftHandSides()) {
            for (HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                ProgramState abstractedState =
                        canonicalizationHelper.tryReplaceMatching(state, rhs, lhs);
                if (abstractedState != null) {
                    return performCanonicalization(abstractedState);
                }
            }
        }
        return state;
    }


}
