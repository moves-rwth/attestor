package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateCanonicalizationStrategy;

public class MockupStateCanonicalizationStrategy extends StateCanonicalizationStrategy {

    public MockupStateCanonicalizationStrategy() {
        super(null);
    }

    @Override
    public ProgramState canonicalize(ProgramState state) {

        return state;
    }

}
