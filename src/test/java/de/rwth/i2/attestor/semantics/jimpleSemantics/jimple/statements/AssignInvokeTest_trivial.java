package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public class AssignInvokeTest_trivial {
	//private static final Logger logger = LogManager.getLogger( "AssignInvokeTest.java" );

	private AssignInvoke stmt;
	private HeapConfiguration inputGraph;
	private DefaultProgramState inputState;


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception{
		Settings.getInstance().grammar().setGrammar( Grammar.builder().build() );
		
		Type type = Settings.getInstance().factory().getType( "node" );
		Local var 
			= new Local( type, "x" );


		AbstractMethod method = new IpaAbstractMethod( "method");
		List<Semantics> defaultControlFlow = new ArrayList<>();
		defaultControlFlow.add( new Skip( -1 ) );
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
			assertEquals( inputGraph, resState.getHeap());
		}catch( NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e ){
			fail("unexpected exception: " + e.getMessage());
		}
	}
	


	@Test
	public void testNeedsMaterialization(){
		assertFalse( stmt.needsMaterialization( inputState ) );
	}

	@Test
	public void testToString(){
		assertEquals( "x = method();", stmt.toString() );
	}
}
