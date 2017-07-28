package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.tasks.GeneralConcreteValue;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public class FieldTest {

	private Field expr;
	private Local local;
	private HeapConfiguration testGraph;
	private GeneralSelectorLabel sel;
	
	
	@Before
	public void setUp() throws Exception {
		
		testGraph = ExampleHcImplFactory.getListAndConstants();
		sel = GeneralSelectorLabel.getSelectorLabel("next");

		Type type = TypeFactory.getInstance().getType("List");		
		local = new Local( type, "x");
		expr = new Field( type, local, "next");
	}

	
	@Test
	public void accessTest() {
		int hash = testGraph.hashCode();
		
		try {
			
			DefaultState executable = new DefaultState( testGraph.clone() );
			executable.prepareHeap();
			
			GeneralConcreteValue res = null;
			try {
				res = (GeneralConcreteValue) expr.evaluateOn(executable);
			} catch (NullPointerDereferenceException e) {
				e.printStackTrace();
			}
						
			assertNotNull("testGraph null", testGraph );
			assertEquals("testGraph changed", hash, testGraph.hashCode());
			assertNotNull( executable );
			
			GeneralConcreteValue
			expectedRes = executable.getVariableTarget("x");
			
			assertNotNull("Variable 'x' should exist.", expectedRes);
			
			int expNode = expectedRes.getNode();
			assertNotNull("Node corresponding to variable x should be defined.", expNode);
			
			expNode = executable.getHeap().selectorTargetOf(expNode, sel);
			assertNotNull("Target of selector 'next' should exist.", expNode);

			assert res != null;
			assertEquals("doesn't return correct node", expNode, res.getNode());
			
			HeapConfiguration resHeapConfig = executable.getHeap();
			
			DefaultState original = new DefaultState( testGraph.clone() );
			original.prepareHeap();
			
			assertTrue("heap should not change", original.getHeap().equals(resHeapConfig));
		} catch (NotSufficientlyMaterializedException e) {
			fail("unexpected exception" + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	@Test
	public void changeSelectorTest(){
		
		int hash = testGraph.hashCode();
		
		DefaultState testState = new DefaultState( testGraph );
		testState.prepareHeap();
		
		try {
			DefaultState executable = testState.clone();
			
			ConcreteValue concreteLocal = local.evaluateOn(executable);
			try {
				expr.setValue(executable, concreteLocal);
			} catch (NullPointerDereferenceException e) {
				e.printStackTrace();
			}

			int res = -1;
			try {
				res = ((GeneralConcreteValue) expr
						.evaluateOn( executable ))
					.getNode();
			} catch (NullPointerDereferenceException e) {
				e.printStackTrace();
			}
			
			
			assertNotNull("testGraph null", testGraph );
			assertEquals("testGraph has changed", hash, testGraph.hashCode());
			assertNotNull("resultHeap null", executable );
			
			int	expectedRes = executable
					.getVariableTarget("x").getNode();
			
			assertEquals("doesn't return correct node", expectedRes, res);			
			
			HeapConfiguration resultHeap = executable.getHeap();
			assertFalse("heap should have changed", testState.getHeap().equals(resultHeap) );
			
			HeapConfiguration expectedGraph = ExampleHcImplFactory.getListAndConstantsWithChange();
			DefaultState expectedState = new DefaultState(expectedGraph);
			expectedState.prepareHeap();
			
			assertTrue("heap not as expected", expectedState.getHeap().equals(resultHeap) );
			
		} catch (NotSufficientlyMaterializedException e) {
			fail("unexpected exception" + e.getMessage() );
			e.printStackTrace();
		}
	
	}


}
