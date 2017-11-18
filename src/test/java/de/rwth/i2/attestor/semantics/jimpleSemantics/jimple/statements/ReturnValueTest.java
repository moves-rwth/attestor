package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ReturnValueTest {
	//private static final Logger logger = LogManager.getLogger( "ReturnValueTest.java" );

	private ReturnValueStmt stmt;
	private HeapConfiguration inputGraph;
	private DefaultProgramState inputState;


	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception{
		Type type = Settings.getInstance().factory().getType( "node" );
		stmt = new ReturnValueStmt( new Local( type, "x" ), type );
		inputState = new DefaultProgramState( ExampleHcImplFactory.getListAndConstants() );
		inputState.prepareHeap();
		inputGraph = inputState.getHeap();
	}

	@Test
	public void testComputeSuccessors(){
		try{
			Set<ProgramState> res = stmt.computeSuccessors( inputState, new MockupSymbolicExecutionObserver() );
			assertEquals( 1, res.size() );
			DefaultProgramState resState = (DefaultProgramState) res.iterator().next();
			assertNotSame("ensure clone on state level", resState, inputState );
			assertNotSame("ensure clone on graph level", inputGraph, resState.getHeap() );
			assertSame("ensure inputGraph still in inputState", inputGraph, inputState.getHeap() );
			DefaultProgramState tmp = new DefaultProgramState( ExampleHcImplFactory.getListAndConstants() );
			tmp.prepareHeap();
			HeapConfiguration expectedGraph = tmp.getHeap();
			assertEquals("ensure inputGraph didn't change", expectedGraph, inputGraph );
		}catch( NotSufficientlyMaterializedException e ){
			fail("Unexpected exception: " + e.getMessage());
		}
		
	}

	@Test
	public void testNeedsMaterialization(){
		assertFalse( stmt.needsMaterialization( inputState ) );
	}

	@Test
	public void testToString(){
		assertEquals("return x;", stmt.toString() );
	}
}
