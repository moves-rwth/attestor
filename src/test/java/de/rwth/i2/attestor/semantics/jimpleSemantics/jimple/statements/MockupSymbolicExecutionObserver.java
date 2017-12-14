package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import java.util.ArrayList;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.*;

public class MockupSymbolicExecutionObserver extends SceneObject implements SymbolicExecutionObserver {

    public MockupSymbolicExecutionObserver(SceneObject sceneObject) {

        super(sceneObject);
    }

    @Override
    public void update(Object handler, ProgramState input) {

    }

    @Override
    public StateSpace generateStateSpace(Program program, ProgramState input) throws StateSpaceGenerationAbortedException {

        ProgramState initialState = new DefaultProgramState(input.getHeap());
        initialState.setProgramCounter(0);
        return StateSpaceGenerator.builder(this)
                .addInitialState(initialState)
                .setProgram(program)
                .setStateRefinementStrategy(new NoStateRefinementStrategy())
                .setAbortStrategy(new StateSpaceBoundedAbortStrategy(500, 50))
                .setStateLabelingStrategy(new NoStateLabelingStrategy())
                .setMaterializationStrategy(
                        (state, potentialViolationPoints) -> new ArrayList<>()
                )
                .setCanonizationStrategy(state -> state.clone())
                .setStateCounter(s -> {
                })
                .setExplorationStrategy((s, sp) -> true)
                .setStateSpaceSupplier(() -> new InternalStateSpace(100))
                .setSemanticsOptionsSupplier(s -> new MockupSymbolicExecutionObserver(this))
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .build()
                .generate();
    }


    @Override
    public boolean isDeadVariableEliminationEnabled() {

        return false;
    }

	@Override
	public StateSpace continueStateSpace(StateSpace stateSpace, Program program, ProgramState continuationPoint)
			throws StateSpaceGenerationAbortedException {
        
		
        return new StateSpaceContinuationGeneratorBuilder(this)
                .addEntryState(continuationPoint)
                .setStateSpaceToContinue(stateSpace)
                .setProgram(program)
                .setStateRefinementStrategy(new NoStateRefinementStrategy())
                .setAbortStrategy(new StateSpaceBoundedAbortStrategy(500, 50))
                .setStateLabelingStrategy(new NoStateLabelingStrategy())
                .setMaterializationStrategy(
                        (state, potentialViolationPoints) -> new ArrayList<>()
                )
                .setCanonizationStrategy(state -> state.clone())
                .setStateCounter(s -> {
                })
                .setExplorationStrategy((s, sp) -> true)
                .setStateSpaceSupplier(() -> new InternalStateSpace(100))
                .setSemanticsOptionsSupplier(s -> new MockupSymbolicExecutionObserver(this))
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .build()
                .generate();
	}
}
