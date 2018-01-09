package de.rwth.i2.attestor;

import de.rwth.i2.attestor.main.scene.DefaultScene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupAbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoCanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateRefinementStrategy;

import java.util.Collections;

public class MockupSceneObject extends SceneObject {

    public MockupSceneObject() {

        super(new DefaultScene());

        scene().options().setRemoveDeadVariables(false);

        scene().strategies().setAbortStrategy(new MockupAbortStrategy());
        scene().strategies().setStateLabelingStrategy(new NoStateLabelingStrategy());
        scene().strategies().setStateRefinementStrategy(new NoStateRefinementStrategy());
        scene().strategies().setMaterializationStrategy((heapConfiguration, potentialViolationPoints) -> Collections.emptySet());
        scene().strategies().setLenientCanonicalizationStrategy(new NoCanonicalizationStrategy());
        scene().strategies().setAggressiveCanonicalizationStrategy(new NoCanonicalizationStrategy());
    }
}
