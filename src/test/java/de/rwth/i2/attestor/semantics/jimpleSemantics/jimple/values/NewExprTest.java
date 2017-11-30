package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.environment.SceneObject;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewExprTest {

	private SceneObject sceneObject;
	private ExampleHcImplFactory hcImplFactory;

	private NewExpr expr;
	private HeapConfiguration testGraph;

	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp(){

		sceneObject = new MockupSceneObject();
		hcImplFactory = new ExampleHcImplFactory(sceneObject);
		Type type = sceneObject.scene().getType( "NewExprTestNode");
        BasicSelectorLabel next = BasicSelectorLabel.getSelectorLabel("next");
        type.addSelectorLabel(next.getLabel(), Constants.NULL);
		expr = new NewExpr(type);
		testGraph = hcImplFactory.getThreeElementDLLWithConstants();
	}

	@Test
	public void test() {
		
		int hash = testGraph.hashCode();
		int oldNodeNumber = testGraph.countNodes();
				
		try {
			DefaultProgramState executable = new DefaultProgramState(testGraph.clone());
			
			expr.evaluateOn( executable );
			
			assertNotNull("testGraph null", testGraph );
			assertEquals("testGraph changed", hash, testGraph.hashCode());
			assertEquals("node number did not increase by one", oldNodeNumber + 1, executable.getHeap().countNodes());
			assertFalse( testGraph.equals(executable.getHeap()));
			HeapConfiguration expected = hcImplFactory.getExepectedResultTestNewExprTest();
			assertEquals(expected, executable.getHeap());
		} catch (NotSufficientlyMaterializedException e) {
			fail("unexpected exception");
			e.printStackTrace();
		}
	
	}

}
