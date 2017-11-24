package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class MockupCanonicalizationStrategy implements CanonicalizationStrategy {

	@Override
	public ProgramState canonicalize(ProgramState state) {
		return state;
	}

}
