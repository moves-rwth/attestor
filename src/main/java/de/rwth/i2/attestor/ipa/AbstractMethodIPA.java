package de.rwth.i2.attestor.ipa;

import java.util.Set;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.util.Pair;

public class AbstractMethodIPA extends AbstractMethod {

	public AbstractMethodIPA(String displayName, StateSpaceFactory factory) {
		super(displayName, factory);
	}

	@Override
	public Set<ProgramState> getResult(HeapConfiguration input, int scopeDepth) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Pair<HeapConfiguration, HeapConfiguration> prepareInput( HeapConfiguration input ){
		ReachableFragmentComputer helper = new ReachableFragmentComputer( displayName );
		return helper.prepareInput(input);
	}

}
