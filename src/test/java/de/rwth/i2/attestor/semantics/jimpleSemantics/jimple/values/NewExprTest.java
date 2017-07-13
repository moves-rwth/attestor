package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import static org.junit.Assert.*;

import org.junit.*;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultState;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public class NewExprTest {
	
	private NewExpr expr;
	private HeapConfiguration testGraph;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
	}
	
	@Before
	public void setUp(){
		expr = new NewExpr( TypeFactory.getInstance().getType( "node"));
		testGraph = ExampleHcImplFactory.getThreeElementDLL();
	}

	@Test
	public void test() {
		
		int hash = testGraph.hashCode();
		int oldNodeNumber = testGraph.countNodes();
				
		try {
			DefaultState executable = new DefaultState(testGraph.clone());
			
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
