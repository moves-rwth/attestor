package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import java.util.Set;

import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.util.SingleElementUtil;

public class MockupCanonicalizationStrategy implements CanonicalizationStrategy {
	//private static final Logger logger = LogManager.getLogger( "TestCanonizationStrategy" );
	
	@Override
	public Set<ProgramState> canonicalize(Semantics semantics, ProgramState conf ) {
		return SingleElementUtil.createSet( conf );
	}
	


	



}
