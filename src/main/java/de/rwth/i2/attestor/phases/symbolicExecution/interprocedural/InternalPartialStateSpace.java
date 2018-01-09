package de.rwth.i2.attestor.phases.symbolicExecution.interprocedural;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.interprocedural.interproceduralAnalysis.PartialStateSpace;
import de.rwth.i2.attestor.phases.symbolicExecution.interprocedural.interproceduralAnalysis.ProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContract;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.ArrayList;
import java.util.List;

public class InternalPartialStateSpace implements PartialStateSpace {

    private ProgramState callingState;
    private StateSpaceGeneratorFactory stateSpaceGeneratorFactory;

    public InternalPartialStateSpace(ProgramState callingState,
                                     StateSpaceGeneratorFactory stateSpaceGeneratorFactory) {

        this.callingState = callingState;
        this.stateSpaceGeneratorFactory = stateSpaceGeneratorFactory;
    }

    @Override
    public void continueExecution(ProcedureCall call) {

        try {

            Method method = call.getMethod();
            ProgramState preconditionState = call.getInput();

            StateSpace stateSpace = stateSpaceGeneratorFactory.create(
                    call.getMethod().getBody(),
                    callingState,
                    callingState.getContainingStateSpace()
            ).generate();

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

        return (callingState == null) ? 0 : callingState.getContainingStateSpace().hashCode();
    }

    public boolean equals(Object otherObject) {

        if(this == otherObject) {
            return true;
        }
        if(otherObject == null) {
            return false;
        }
        if(otherObject.getClass() != InternalPartialStateSpace.class) {
            return false;
        }
        if(callingState == null) {
            return false;
        }
        InternalPartialStateSpace other = (InternalPartialStateSpace) otherObject;
        if(other.callingState == null) {
            return false;
        }

        return callingState.getContainingStateSpace() == other.callingState.getContainingStateSpace();

    }

}
