package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.SettableValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

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
		
		
		DefaultProgramState tmp = new DefaultProgramState(testGraph);
		
		tmp.prepareHeap();
		testGraph = tmp.getHeap();
		
		String test = testGraph.toString();
		BasicSelectorLabel sel = BasicSelectorLabel.getSelectorLabel("right");
		Type type = Settings.getInstance().factory().getType( "node" );

		SettableValue lhs = new Local( type, "XYZ" );		
		Value origin = new Local( type, "ZYX" );
		Value rhs = new Field( type, origin, "right" );

		AssignStmt stmt = new AssignStmt(lhs, rhs, 2, new HashSet<>());
		try{
			
			DefaultProgramState input = new DefaultProgramState(testGraph);
			
			Set<ProgramState> res = stmt.computeSuccessors( input, new MockupSymbolicExecutionObserver() );
			
			assertNotNull( "test graph became null", testGraph);
			assertEquals( "testGraph has changed", test, testGraph.toString() );

			assertTrue( "res > 1", res.size() == 1 );
			
			for(ProgramState resProgramState : res) {
				
				DefaultProgramState resState = (DefaultProgramState) resProgramState;
				
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
				DefaultProgramState tmpState = new DefaultProgramState(expectedHeap);
				tmpState.prepareHeap();
				expectedHeap = tmpState.getHeap();
				assertEquals( expectedHeap, resState.getHeap());

			}
			

		}catch( NotSufficientlyMaterializedException e ){
			fail( "Unexpected Exception: " + Arrays.toString(e.getStackTrace()));
		}

	}

}
