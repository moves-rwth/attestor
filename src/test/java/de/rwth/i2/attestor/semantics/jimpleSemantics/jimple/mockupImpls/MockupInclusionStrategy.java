package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.InclusionStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class MockupInclusionStrategy implements InclusionStrategy {
	//private static final Logger logger = LogManager.getLogger( "TestInclusionStrategy" );

	
	@Override
	public boolean isIncludedIn( ProgramState left, ProgramState right ) {

		return left.equals(right);
	}
}
