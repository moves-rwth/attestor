package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NewExpr;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public class AssignInvokeTest_NonTrivial {

	private AssignInvoke stmt;
	private HeapConfiguration inputGraph;
	private DefaultProgramState inputState;

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception{
		Type type = Settings.getInstance().factory().getType( "node" );
		Local var = new Local( type, "x" );

		AbstractMethod method= IpaAbstractMethod.getMethod( "method");
		List<Semantics> defaultControlFlow = new ArrayList<>();
		defaultControlFlow.add(new AssignStmt(var, new NewExpr(type), 1, new HashSet<>()));
		defaultControlFlow.add( new ReturnValueStmt(var, type) );
		method.setControlFlow( defaultControlFlow );
		InvokeHelper invokePrepare = new StaticInvokeHelper( new ArrayList<>());
		
		stmt = new AssignInvoke( var, method, invokePrepare, 1 );
		
		inputGraph = ExampleHcImplFactory.getListAndConstants();
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
			assertEquals( ExampleHcImplFactory.getListAndConstants(), inputGraph );
			assertFalse( inputGraph.equals( resState.getHeap() ) );
			assertEquals(ExampleHcImplFactory.getExpectedResult_AssignInvokeNonTrivial(), resState.getHeap());
		}catch( NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e ){
			fail("unexpected exception: " + e.getMessage());
		}
	}
	


	@Test
	public void testNeedsMaterialization(){
		assertFalse( stmt.needsMaterialization( inputState ) );
	}
}
