package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import static org.junit.Assert.*;

import java.util.*;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.environment.SceneObject;
import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NewExpr;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public class AssignInvokeTest_NonTrivial {

	private SceneObject sceneObject;
	private ExampleHcImplFactory hcFactory;

	private AssignInvoke stmt;
	private HeapConfiguration inputGraph;
	private DefaultProgramState inputState;

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception{

		sceneObject = new MockupSceneObject();
		hcFactory = new ExampleHcImplFactory(sceneObject);

		Type type = sceneObject.scene().getType( "AssignInvokeTestNonTrivial" );

		SelectorLabel next = sceneObject.scene().getSelectorLabel("next");
		SelectorLabel prev = sceneObject.scene().getSelectorLabel("prev");
		type.addSelectorLabel(next, Constants.NULL);
		type.addSelectorLabel(prev, Constants.NULL);

		Local var = new Local( type, "x" );

		AbstractMethod method= sceneObject.scene().getMethod( "method");
		List<Semantics> defaultControlFlow = new ArrayList<>();
		defaultControlFlow.add(new AssignStmt(var, new NewExpr(type), 1, new HashSet<>()));
		defaultControlFlow.add( new ReturnValueStmt(var, type) );
		method.setControlFlow( defaultControlFlow );
		InvokeHelper invokePrepare = new StaticInvokeHelper( new ArrayList<>());
		
		stmt = new AssignInvoke( var, method, invokePrepare, 1 );
		
		inputGraph = hcFactory.getListAndConstants();
		inputState = new DefaultProgramState( inputGraph );
	}

	@Test
	public void testComputeSuccessors(){
		try{
			Set<ProgramState> resStates = stmt.computeSuccessors( inputState, new MockupSymbolicExecutionObserver() );
			assertEquals( 1, resStates.size() );
			DefaultProgramState resState = (DefaultProgramState) resStates.iterator().next();
			assertNotSame( resState, inputState );
			assertNotSame( inputGraph, resState.getHeap() );
			assertSame( inputGraph, inputState.getHeap() );
			assertEquals( hcFactory.getListAndConstants(), inputGraph );
			assertFalse( inputGraph.equals( resState.getHeap() ) );
			assertEquals(hcFactory.getExpectedResult_AssignInvokeNonTrivial(), resState.getHeap());
		}catch( NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e ){
			fail("unexpected exception: " + e.getMessage());
		}
	}
	


	@Test
	public void testNeedsMaterialization(){
		assertFalse( stmt.needsMaterialization( inputState ) );
	}
}
