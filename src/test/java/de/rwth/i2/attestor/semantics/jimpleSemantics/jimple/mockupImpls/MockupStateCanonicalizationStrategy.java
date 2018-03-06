package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateCanonicalizationStrategy;

import java.util.Collection;
import java.util.Collections;

public class MockupStateCanonicalizationStrategy implements StateCanonicalizationStrategy {

    @Override
    public Collection<ProgramState> canonicalize(ProgramState state) {

        return Collections.singleton(state);
    }

}
