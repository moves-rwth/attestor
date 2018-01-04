package de.rwth.i2.attestor.main.phases.interprocedural;

import de.rwth.i2.attestor.interprocedural.ProcedureCall;
import de.rwth.i2.attestor.main.phases.stateSpaceGeneration.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.List;

public class MainProcedureCall implements ProcedureCall {

    private Program body;
    private List<ProgramState> preconditionStates;
    private StateSpaceGeneratorFactory factory;
    private StateSpace stateSpace;

    public MainProcedureCall(Program body, List<ProgramState> preconditionStates, StateSpaceGeneratorFactory factory) {

        this.body = body;
        this.preconditionStates = preconditionStates;
        this.factory = factory;
    }


    @Override
    public void execute() {

        try {
            stateSpace = factory.create(body, preconditionStates).generate();
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Procedure call execution failed.");
        }
    }

    public StateSpace getStateSpace() {

        return stateSpace;
    }
}
