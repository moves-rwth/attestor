package de.rwth.i2.attestor.main.phases.symbolicExecution.interprocedural;

import de.rwth.i2.attestor.interprocedural.PartialStateSpace;
import de.rwth.i2.attestor.interprocedural.ProcedureCall;
import de.rwth.i2.attestor.main.phases.symbolicExecution.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

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
            stateSpaceGeneratorFactory.create(
                    method.getBody(),
                    preconditionState,
                    callingState.getStateSpace()
            ).generate();


        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Failed to continue state space execution.");
        }

        return new InternalProcedureCall(method, preconditionState, stateSpaceGeneratorFactory);
    }

}
