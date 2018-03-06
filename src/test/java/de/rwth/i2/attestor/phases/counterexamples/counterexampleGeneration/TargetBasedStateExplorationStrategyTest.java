package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupCanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TargetBasedStateExplorationStrategyTest {

    private SceneObject sceneObject;
    private ProgramState trivialState;

    @Before
    public void setUp() {

        this.sceneObject = new MockupSceneObject();

        HeapConfiguration emptyHc = sceneObject.scene().createHeapConfiguration();
        trivialState = sceneObject.scene().createProgramState(emptyHc);
    }

    @Test
    public void testTarget() {

        Collection<ProgramState> targetStates = new ArrayList<>();
        targetStates.add(trivialState);

        ProgramState otherState = trivialState.clone();
        otherState.setProgramCounter(12);
        targetStates.add(otherState);

        StateSubsumptionStrategy subsumptionStrategy = new StateSubsumptionStrategy(new MockupCanonicalizationStrategy());

        TargetBasedStateExplorationStrategy strategy = new TargetBasedStateExplorationStrategy(targetStates, subsumptionStrategy);

        assertFalse(strategy.hasUnexploredStates());
        strategy.addUnexploredState(otherState, false);
        assertTrue(strategy.hasUnexploredStates());
        strategy.getNextUnexploredState();
        strategy.addUnexploredState(trivialState, false);
        assertFalse(strategy.hasUnexploredStates());
        strategy.addUnexploredState(trivialState, false);
        assertFalse(strategy.hasUnexploredStates());
        strategy.addUnexploredState(otherState, false);
        assertFalse(strategy.hasUnexploredStates());

    }

}
