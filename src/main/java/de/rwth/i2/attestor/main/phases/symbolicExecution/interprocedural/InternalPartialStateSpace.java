package de.rwth.i2.attestor.main.phases.symbolicExecution.interprocedural;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.interprocedural.PartialStateSpace;
import de.rwth.i2.attestor.interprocedural.ProcedureCall;
import de.rwth.i2.attestor.main.phases.symbolicExecution.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.contracts.InternalContract;
import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.ArrayList;
import java.util.List;

public class InternalPartialStateSpace implements PartialStateSpace {

    private ProgramState callingState;
    private Method method;
    private ProgramState preconditionState;
    private StateSpaceGeneratorFactory stateSpaceGeneratorFactory;

    public InternalPartialStateSpace(ProgramState callingState, Method method, ProgramState preconditionState,
                                     StateSpaceGeneratorFactory stateSpaceGeneratorFactory) {

        this.callingState = callingState;
        this.method = method;
        this.preconditionState = preconditionState;
        this.stateSpaceGeneratorFactory = stateSpaceGeneratorFactory;
    }

    @Override
    public ProcedureCall continueExecution() {

        try {
            StateSpace stateSpace = stateSpaceGeneratorFactory.create(
                    method.getBody(),
                    preconditionState,
                    callingState.getContainingStateSpace()
            ).generate();

            List<HeapConfiguration> finalHeaps = new ArrayList<>();
            stateSpace.getFinalStates().forEach( finalState -> finalHeaps.add(finalState.getHeap()) );
            Contract contract = new InternalContract(preconditionState.getHeap(), finalHeaps);
            method.addContract(contract);

        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Failed to continue state space execution.");
        }

        return new InternalProcedureCall(method, preconditionState, stateSpaceGeneratorFactory);
    }

}
