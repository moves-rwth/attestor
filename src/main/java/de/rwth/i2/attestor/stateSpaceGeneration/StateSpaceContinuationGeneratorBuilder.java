package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.main.scene.SceneObject;

public class StateSpaceContinuationGeneratorBuilder extends SSGBuilder {

	public StateSpaceContinuationGeneratorBuilder(SceneObject sceneObject) {
		super(sceneObject);
		statesToContinue = new ArrayList<>();
	}
	
    /**
     * The initial state passed to the state space generation
     */
    private final List<ProgramState> statesToContinue;
    
    private StateSpace stateSpaceToContinue;
	
    /**
     * @param stateToContinue The initial state from which all reachable states are computed by
     *                     the state space generation.
     * @return The builder.
     */
    public StateSpaceContinuationGeneratorBuilder addInitialState( ProgramState stateToContinue ) {

        statesToContinue.add(stateToContinue);
        return this;
    }

    /**
     * @param statesToContinue The initial states from which all reachable states are computed by
     *                      the state space generation.
     * @return The builder.
     */
    public StateSpaceContinuationGeneratorBuilder addInitialStates(List<ProgramState> statesToContinue) {

        this.statesToContinue.addAll(statesToContinue);
        return this;
    }
    
    public StateSpaceContinuationGeneratorBuilder setStateSpaceToContinue( StateSpace stateSpace ) {
    	this.stateSpaceToContinue = stateSpace;
    	return this;
    }
    
	 /**
     * Attempts to construct a new StateSpaceGenerator.
     * If the initialization is incomplete or invalid
     * calling this method causes an IllegalStateException.
     *
     * @return StateSpaceGenerator initialized by the previously called
     * methods of this builder
     */
    public StateSpaceGenerator build() {

        if ( statesToContinue.isEmpty() ) {
            throw new IllegalStateException("StateSpaceGenerator: No initial states.");
        }

        if ( super.generator.program == null) {
            throw new IllegalStateException("StateSpaceGenerator: No program.");
        }

        if ( super.generator.materializationStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No materialization strategy.");
        }

        if (generator.canonicalizationStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No canonicalization strategy.");
        }

        if (generator.abortStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No abort strategy.");
        }

        if (generator.stateLabelingStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No state labeling strategy.");
        }

        if (generator.stateRefinementStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No state refinement strategy.");
        }

        if (generator.totalStatesCounter == null) {
            throw new IllegalStateException("StateSpaceGenerator: No state counter.");
        }

        if (generator.explorationStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No exploration strategy.");
        }

        if (generator.semanticsObserverSupplier == null) {
            throw new IllegalStateException("StateSpaceGenerator: No supplier for semantics options.");
        }

        if (generator.stateSpaceSupplier == null) {
            throw new IllegalStateException("StateSpaceGenerator: No supplier for state spaces.");
        }

        if (generator.postProcessingStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No post-processing strategy.");
        }

        generator.stateSpace = stateSpaceToContinue;

        for (ProgramState state : statesToContinue ) {
            generator.stateLabelingStrategy.computeAtomicPropositions(state);
            generator.addUnexploredState(state);
        }
        
        for (ProgramState state : super.initialStates ) {
            state.setProgramCounter(0);
            generator.stateLabelingStrategy.computeAtomicPropositions(state);
            generator.stateSpace.addInitialState(state);
            generator.addUnexploredState(state);
        }
       
        generator.symbolicExecutionObserver = generator.semanticsObserverSupplier.get(generator);
        return generator;
    }

}
