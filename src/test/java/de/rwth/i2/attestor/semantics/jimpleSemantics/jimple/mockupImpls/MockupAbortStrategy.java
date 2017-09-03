package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.stateSpace.StateSpace;

public class MockupAbortStrategy implements AbortStrategy {
	//private static final Logger logger = LogManager.getLogger( "TestAbortStrategy" );
	
	@Override
	public boolean isAllowedToContinue(StateSpace stateSpace ) {
		
		return stateSpace.getStates().size() < 50;
	}
	

	
}
