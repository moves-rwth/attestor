package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public class AssignStmtTest {

	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
		Settings.getInstance().options().setRemoveDeadVariables(false);
	}

	@Test
	public void test(){
		HeapConfiguration testGraph = ExampleHcImplFactory.getTLLRule();
		
		
		DefaultState tmp = new DefaultState(testGraph);
		
		tmp.prepareHeap();
		testGraph = tmp.getHeap();
		
		String test = testGraph.toString();
		GeneralSelectorLabel sel = GeneralSelectorLabel.getSelectorLabel("right");
		Type type = TypeFactory.getInstance().getType( "node" );

		SettableValue lhs = new Local( type, "XYZ" );		
		Value origin = new Local( type, "ZYX" );
		Value rhs = new Field( type, origin, "right" );

		AssignStmt stmt = new AssignStmt(lhs, rhs, 2, new HashSet<>());
		try{
			
			DefaultState input = new DefaultState(testGraph);
			
			Set<ProgramState> res = stmt.computeSuccessors( input );
			
			assertNotNull( "test graph became null", testGraph);
			assertEquals( "testGraph has changed", test, testGraph.toString() );

			assertTrue( "res > 1", res.size() == 1 );
			
			for(ProgramState resProgramState : res) {
				
				DefaultState resState = (DefaultState) resProgramState;
				
				assertTrue( "nextPC != 2", resState.getProgramCounter() == 2 );
				
				assertNotNull( "resConfig null", resState.getHeap() );
				
				HeapConfiguration hc = resState.getHeap();
				
				int varZYX = hc.variableWith("0-ZYX");
				int targetZYX = hc.targetOf(varZYX);
				int expectedNode = hc.selectorTargetOf(targetZYX, sel);
				
				int varXYZ = hc.variableWith("0-XYZ");
				int actualNode = hc.targetOf(varXYZ);

				assertEquals( "selector not set as expected", expectedNode, actualNode );
				assertFalse( resState.getHeap().equals(input.getHeap()) );
								
				HeapConfiguration expectedHeap = ExampleHcImplFactory.getExpectedResult_AssignStmt();
				DefaultState tmpState = new DefaultState(expectedHeap);
				tmpState.prepareHeap();
				expectedHeap = tmpState.getHeap();
				assertEquals( expectedHeap, resState.getHeap());

			}
			

		}catch( NotSufficientlyMaterializedException e ){
			fail( "Unexpected Exception: " + Arrays.toString(e.getStackTrace()));
		}

	}

}
