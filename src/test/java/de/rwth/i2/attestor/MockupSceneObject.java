package de.rwth.i2.attestor;

import de.rwth.i2.attestor.main.scene.DefaultScene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoCanonicalizationStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoRectificationStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoStateLabelingStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoStateRefinementStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupAbortStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupCanonicalizationStrategy;

import java.util.Collections;

public class MockupSceneObject extends SceneObject {

    public MockupSceneObject() {

        super(new DefaultScene());

        scene().options().setRemoveDeadVariables(false);

        scene().strategies().setAbortStrategy(new MockupAbortStrategy());
        scene().strategies().setStateLabelingStrategy(new NoStateLabelingStrategy());
        scene().strategies().setStateRefinementStrategy(new NoStateRefinementStrategy());
        scene().strategies().setMaterializationStrategy((heapConfiguration, potentialViolationPoints) -> Collections.emptySet());
        scene().strategies().setStateRectificationStrategy(new NoRectificationStrategy());
        scene().strategies().setAggressiveCanonicalizationStrategy(new NoCanonicalizationStrategy());
        scene().strategies().setCanonicalizationStrategy(new MockupCanonicalizationStrategy());
    }
}
