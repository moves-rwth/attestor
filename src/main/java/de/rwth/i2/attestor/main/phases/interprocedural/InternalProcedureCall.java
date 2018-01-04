package de.rwth.i2.attestor.main.phases.interprocedural;

import de.rwth.i2.attestor.interprocedural.ProcedureCall;
import de.rwth.i2.attestor.main.phases.stateSpaceGeneration.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

public class InternalProcedureCall implements ProcedureCall {

    private Method method;
    private ProgramState preconditionState;
    private StateSpaceGeneratorFactory factory;
    private StateSpace stateSpace;

    public InternalProcedureCall(Method method, ProgramState preconditionState, StateSpaceGeneratorFactory factory) {

        this.method = method;
        this.preconditionState = preconditionState;
        this.factory = factory;
    }


    @Override
    public void execute() {

        try {
            stateSpace = factory.create(method.getBody(), preconditionState).generate();
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Procedure call execution failed.");
        }
    }

    public StateSpace getStateSpace() {

        return stateSpace;
    }
}
