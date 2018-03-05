package de.rwth.i2.attestor.phases.symbolicExecution.recursive;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContract;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.PartialStateSpace;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureCall;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.ArrayList;
import java.util.List;

public class InternalPartialStateSpace implements PartialStateSpace {

    private ProgramState stateToContinue;
    private StateSpaceGeneratorFactory stateSpaceGeneratorFactory;
    StateSpace partialStateSpace;

    public InternalPartialStateSpace(ProgramState callingState,
                                     StateSpaceGeneratorFactory stateSpaceGeneratorFactory) {

        this.stateToContinue = callingState;
        this.partialStateSpace = callingState.getContainingStateSpace();
        this.stateSpaceGeneratorFactory = stateSpaceGeneratorFactory;
    }

    @Override
    public void continueExecution(ProcedureCall call) {

        try {

            Method method = call.getMethod();
            ProgramState preconditionState = call.getInput();

            if(partialStateSpace.containsAbortedStates()) {
                return;
            }
            
            stateToContinue.flagAsContinueState();

            StateSpace stateSpace = stateSpaceGeneratorFactory.create(
                    call.getMethod().getBody(),
                    stateToContinue,
                    partialStateSpace
            ).generate();
            
            stateToContinue.unflagContinueState();

            List<HeapConfiguration> finalHeaps = new ArrayList<>();
            stateSpace.getFinalStates().forEach( finalState -> finalHeaps.add(finalState.getHeap()) );
            Contract contract = new InternalContract(preconditionState.getHeap(), finalHeaps);
            method.addContract(contract);

        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Failed to continue state space execution.");
        }
    }

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((partialStateSpace == null) ? 0 : partialStateSpace.hashCode());
		result = prime * result + ((stateToContinue == null) ? 0 : stateToContinue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InternalPartialStateSpace other = (InternalPartialStateSpace) obj;
		if (partialStateSpace == null) {
			if (other.partialStateSpace != null)
				return false;
		} else if (!partialStateSpace.equals(other.partialStateSpace))
			return false;
		if (stateToContinue == null) {
			if (other.stateToContinue != null)
				return false;
		} else if (!stateToContinue.equals(other.stateToContinue))
			return false;
		return true;
	}

	@Override
	public StateSpace unfinishedStateSpace() {
		return this.partialStateSpace;
	}

    

}
