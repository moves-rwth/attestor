package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class StateSubsumptionStrategy {

    private CanonicalizationStrategy canonicalizationStrategy;

    public StateSubsumptionStrategy(CanonicalizationStrategy canonicalizationStrategy) {

        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    public boolean subsumes(ProgramState subsumed, ProgramState subsuming) {

        if(subsumed.getProgramCounter() != subsuming.getProgramCounter()) {
            return false;
        }

        HeapConfiguration left = subsumed.getHeap();
        HeapConfiguration right = subsuming.getHeap();
        HeapConfiguration abstractedLeft = canonicalizationStrategy.canonicalize(left);

        return abstractedLeft.equals(right);
    }
}
