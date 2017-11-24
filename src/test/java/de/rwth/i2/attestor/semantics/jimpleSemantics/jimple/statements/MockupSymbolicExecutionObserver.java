package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoPostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.StateSpaceBoundedAbortStrategy;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;

import java.util.ArrayList;

public class MockupSymbolicExecutionObserver implements SymbolicExecutionObserver {


    @Override
    public void update(Object handler, ProgramState input) {

    }

    @Override
    public StateSpace generateStateSpace(Program program, ProgramState input) throws StateSpaceGenerationAbortedException {

        ProgramState initialState = new DefaultProgramState(input.getHeap(), input.getScopeDepth());
        initialState.setProgramCounter(0);
        return StateSpaceGenerator.builder()
                .addInitialState(initialState)
                .setProgram(program)
                .setStateRefinementStrategy(new NoStateRefinementStrategy())
                .setAbortStrategy(new StateSpaceBoundedAbortStrategy(500, 50))
                .setStateLabelingStrategy(new NoStateLabelingStrategy())
                .setMaterializationStrategy(
                        (state, potentialViolationPoints) -> new ArrayList<>()
                )
                .setCanonizationStrategy((semantics, state) -> state.clone())
                .setStateCounter( s -> {} )
                .setExplorationStrategy((s,sp) -> true)
                .setStateSpaceSupplier(() -> new InternalStateSpace(100))
                .setSemanticsOptionsSupplier(s -> new MockupSymbolicExecutionObserver())
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .build()
                .generate();
    }


    @Override
    public boolean isDeadVariableEliminationEnabled() {
        return false;
    }
}
