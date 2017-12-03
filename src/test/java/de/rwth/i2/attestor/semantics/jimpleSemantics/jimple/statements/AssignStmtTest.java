package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import static org.junit.Assert.*;

import java.util.*;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.environment.SceneObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public class AssignStmtTest {


	private SceneObject sceneObject;
	private ExampleHcImplFactory hcFactory;

	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() {
		sceneObject = new MockupSceneObject();
		sceneObject.scene().options().setRemoveDeadVariables(false);
		hcFactory = new ExampleHcImplFactory(sceneObject);
	}

	@Test
	public void test(){
		HeapConfiguration testGraph = hcFactory.getTLLRule();
		
		
		DefaultProgramState tmp = new DefaultProgramState(testGraph);
		
		tmp.prepareHeap();
		testGraph = tmp.getHeap();
		
		String test = testGraph.toString();
		SelectorLabel right = sceneObject.scene().getSelectorLabel("right");
		Type type = sceneObject.scene().getType("node");

		SettableValue lhs = new Local( type, "XYZ" );		
		Value origin = new Local( type, "ZYX" );
		Value rhs = new Field( type, origin, right );

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
				
				int varZYX = hc.variableWith("ZYX");
				int targetZYX = hc.targetOf(varZYX);
				int expectedNode = hc.selectorTargetOf(targetZYX, right);
				
				int varXYZ = hc.variableWith("XYZ");
				int actualNode = hc.targetOf(varXYZ);

				assertEquals( "selector not set as expected", expectedNode, actualNode );
				assertFalse( resState.getHeap().equals(input.getHeap()) );
								
				HeapConfiguration expectedHeap = hcFactory.getExpectedResult_AssignStmt();
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
