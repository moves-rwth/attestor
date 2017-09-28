package de.rwth.i2.attestor.ipa;

import java.util.*;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.util.Pair;

public class AbstractMethodIPA extends AbstractMethod {

	private Map<Pair<String, HeapConfiguration>, Integer> knownMethodCalls;
	private Map<Integer,List<HeapConfiguration>> returnStates;
	private Map<Integer,Pair<Integer,Integer>> callers;
	private Map<Integer,StateSpace> stateSpaces;
	
	public AbstractMethodIPA(String displayName, StateSpaceFactory factory) {
		super(displayName, factory);
	}

	@Override
	public Set<ProgramState> getResult(HeapConfiguration input, int scopeDepth) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<ProgramState> getResult( int idOfCurrentState, int idOfCurrentStateSpace, 
			String methodSignature ) {
		
		StateSpace currentStateSpace = stateSpaces.get( idOfCurrentStateSpace );
		ProgramState currentState = currentStateSpace.getState( idOfCurrentState );
		
		Pair<HeapConfiguration,HeapConfiguration> methodConfig = prepareInput( currentState.getHeap() );
		//TODO store replaced fragment somewhere
		return null;
	}

	protected Pair<HeapConfiguration, HeapConfiguration> prepareInput( HeapConfiguration input ){
		ReachableFragmentComputer helper = new ReachableFragmentComputer( displayName );
		return helper.prepareInput(input);
	}

}
