package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;
import java.util.function.Predicate;

public class MockupFinalStatePredicate implements Predicate<ProgramState> {

    private final Collection<ProgramState> requiredStates;

    public MockupFinalStatePredicate(Collection<ProgramState> requiredStates) {

        this.requiredStates = requiredStates;
    }

    @Override
    public boolean test(ProgramState state) {

        return requiredStates.contains(state);
    }
}
