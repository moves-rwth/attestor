package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewExprTest {
	
	private NewExpr expr;
	private HeapConfiguration testGraph;

	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp(){
		expr = new NewExpr( Settings.getInstance().factory().getType( "node"));
		testGraph = ExampleHcImplFactory.getThreeElementDLL();
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
			assertEquals(ExampleHcImplFactory.getExepectedResultTestNewExprTest(), executable.getHeap());
		} catch (NotSufficientlyMaterializedException e) {
			fail("unexpected exception");
			e.printStackTrace();
		}
	
	}

}
