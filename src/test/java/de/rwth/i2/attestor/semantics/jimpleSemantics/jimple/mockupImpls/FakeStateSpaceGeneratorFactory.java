package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.StateSpaceGeneratorFactory;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.*;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGeneratorBuilder;

/**
 * Uses a fixed set of strategies instead of requesting them from scene.
 * @author Hannah
 *
 */
public class FakeStateSpaceGeneratorFactory extends StateSpaceGeneratorFactory {

	public FakeStateSpaceGeneratorFactory(Scene scene) {
		super(scene);
	}

	protected StateSpaceGeneratorBuilder createBuilder() {
		
		return StateSpaceGenerator.builder()
                .setCanonizationStrategy(new MockupStateCanonicalizationStrategy())
				.setStateRectificationStrategy(new NoRectificationStrategy())
                .setMaterializationStrategy(new MockupMaterializationStrategy())
                .setAbortStrategy(new MockupAbortStrategy())
                .setPostProcessingStrategy(originalStateSpace -> {
                })
                .setStateExplorationStrategy(new DepthFirstStateExplorationStrategy())
                .setStateRefinementStrategy(new NoStateRefinementStrategy())
                .setStateLabelingStrategy(new MockupStateLabellingStrategy())
                .setStateCounter(new NoStateCounter())
                .setStateSpaceSupplier(new MockupStateSpaceSupplier())
                .setFinalStateStrategy(new TerminalStatementFinalStateStrategy());
	}
}
