package de.rwth.i2.attestor.abstraction;

import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public class MockupAbortStrategy implements AbortStrategy {
	
	private boolean abstractionHappened = false;
	private boolean reachedLimit = false;
	
	
	@Override
	public boolean isAllowedToContinue(StateSpace stateSpace) {
			
		for(ProgramState state : stateSpace.getStates()) {
			
			if(state.getHeap().countNonterminalEdges() > 0) {
				
				abstractionHappened = true;
			}
			
			if(state.getHeap().countNodes() > 15) {
				
				reachedLimit = true;
				return false;
			}	
		}
		
		return true;
	}
	
	public boolean hasAbstractedSomeState() {
		
		return abstractionHappened;
	}

	public boolean hasReachedLimit() {
		
		return reachedLimit;
	}
}
