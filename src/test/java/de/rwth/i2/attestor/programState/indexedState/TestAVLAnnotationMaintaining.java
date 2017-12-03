package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.refinement.balanced.BalancednessStateRefinementStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestAVLAnnotationMaintaining {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() {

        SceneObject sceneObject = new MockupSceneObject();
        ExampleIndexedGraphFactory graphFactory = new ExampleIndexedGraphFactory(sceneObject);

        IndexedState input = new IndexedState(graphFactory.getInput_AnnotationMaintaining());
        IndexedState expected = new IndexedState(graphFactory.getExpected_AnnotationMaintaining());

        BalancednessStateRefinementStrategy strategy = new BalancednessStateRefinementStrategy(sceneObject);
        strategy.refine(new Skip(sceneObject, 0), input);

        assertEquals(expected, input);
    }

}
