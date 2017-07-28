package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import java.util.HashSet;
import java.util.Set;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;

public class MockupStateLabellingStrategy implements StateLabelingStrategy {
	//private static final Logger logger = LogManager.getLogger( "TestStateLabellingStrategy" );

	@Override
	public Set<String> computeAtomicPropositions(ProgramState programState ) {
		return new HashSet<>();
	}


}
