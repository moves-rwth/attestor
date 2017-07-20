package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

public class ItentityStatementTest {
	//private static final Logger logger = LogManager.getLogger( "ItentityStatementTest.java" );

	private IdentityStmt stmt;
	private HeapConfiguration inputGraph;
	private DefaultState inputState;

	@Before
	public void setUp() throws Exception{
		Type type = TypeFactory.getInstance().getType( "node" );
		stmt = new IdentityStmt( 1, new Local( type, "y" ), "x" );
		inputState = new DefaultState( ExampleHcImplFactory.getListAndConstants() );
		inputState.prepareHeap();
		inputGraph = inputState.getHeap();
	}

	@Test
	public void testComputeSuccessors(){
		try{
			Set<ProgramState> res = stmt.computeSuccessors( inputState );
			assertEquals( 1, res.size() );
			DefaultState resState = (DefaultState) res.iterator().next();
			assertNotSame("ensure clone on state level", resState, inputState );
			assertNotSame("ensure clone on graph level", inputGraph, resState.getHeap() );
			assertSame("ensure inputGraph still in inputState", inputGraph, inputState.getHeap() );
			DefaultState tmp = new DefaultState( ExampleHcImplFactory.getListAndConstants() );
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
		assertEquals("y = x;", stmt.toString() );
	}
}
