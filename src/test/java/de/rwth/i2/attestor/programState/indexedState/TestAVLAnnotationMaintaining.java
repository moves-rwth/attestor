package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.main.environment.SceneObject;
import de.rwth.i2.attestor.refinement.balanced.BalancednessStateRefinementStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestAVLAnnotationMaintaining {


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {

		SceneObject sceneObject = new MockupSceneObject();
		ExampleIndexedGraphFactory graphFactory = new ExampleIndexedGraphFactory(sceneObject);

		IndexedState input = new IndexedState( graphFactory.getInput_AnnotationMaintaining() );
		IndexedState expected = new IndexedState(graphFactory.getExpected_AnnotationMaintaining());

		BalancednessStateRefinementStrategy strategy = new BalancednessStateRefinementStrategy();
		strategy.refine(new Skip(0), input);

		assertEquals(expected, input);
	}

}
