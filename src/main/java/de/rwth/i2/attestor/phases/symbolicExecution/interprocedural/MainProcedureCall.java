package de.rwth.i2.attestor.phases.symbolicExecution.interprocedural;

import de.rwth.i2.attestor.interprocedural.PartialStateSpace;
import de.rwth.i2.attestor.interprocedural.ProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.util.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.List;

public class MainProcedureCall implements ProcedureCall {

    private Method method;
    private List<ProgramState> preconditionStates;
    private StateSpaceGeneratorFactory factory;
    private StateSpace stateSpace;

    public MainProcedureCall(Method method, List<ProgramState> preconditionStates, StateSpaceGeneratorFactory factory) {

        this.method = method;
        this.preconditionStates = preconditionStates;
        this.factory = factory;
    }


    @Override
    public PartialStateSpace execute() {

        try {
            stateSpace = factory.create(method.getBody(), preconditionStates).generate();
            return new InternalPartialStateSpace(preconditionStates.get(0),factory);
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Procedure call execution failed.");
        }
    }

    @Override
    public Method getMethod() {

        return method;
    }

    @Override
    public ProgramState getInput() {

        return preconditionStates.get(0); // TODO
    }

    public StateSpace getStateSpace() {

        return stateSpace;
    }
}
