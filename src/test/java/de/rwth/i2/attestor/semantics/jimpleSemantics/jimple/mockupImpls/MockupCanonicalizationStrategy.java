package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class MockupCanonicalizationStrategy implements CanonicalizationStrategy {
	//private static final Logger logger = LogManager.getLogger( "TestCanonizationStrategy" );
	
	@Override
	public ProgramState canonicalize(Semantics semantics, ProgramState state) {
		return state;
	}

}
