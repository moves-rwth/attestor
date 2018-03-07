package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;

public class StateCanonicalizationStrategy {

    private CanonicalizationStrategy heapStrategy;

    public StateCanonicalizationStrategy(CanonicalizationStrategy strategy) {

        this.heapStrategy = strategy;
    }

    public CanonicalizationStrategy getHeapStrategy() {
        return heapStrategy;
    }

    public ProgramState canonicalize(ProgramState state) {

        return state.shallowCopyWithUpdateHeap(
                heapStrategy.canonicalize(state.getHeap())
        );
    }
}
