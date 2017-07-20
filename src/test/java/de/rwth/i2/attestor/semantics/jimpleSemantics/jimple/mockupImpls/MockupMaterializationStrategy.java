package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.stateSpaceGeneration.*;

public class MockupMaterializationStrategy implements MaterializationStrategy {
	//private static final Logger logger = LogManager.getLogger( "TestMaterializationStrategy" );

	@Override
	public List<ProgramState> materialize(ProgramState state, ViolationPoints potentialViolationPoints) {
		
		return new ArrayList<>();
	}
	


}
