package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class StateSubsumptionStrategy {

    private CanonicalizationStrategy canonicalizationStrategy;

    public StateSubsumptionStrategy(CanonicalizationStrategy canonicalizationStrategy) {

        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    public boolean subsumes(ProgramState subsumed, ProgramState subsuming) {

        return subsumed.getProgramCounter() == subsuming.getProgramCounter()
                && canonicalizationStrategy.canonicalize(subsumed.getHeap()).equals(subsuming.getHeap());
    }
}
