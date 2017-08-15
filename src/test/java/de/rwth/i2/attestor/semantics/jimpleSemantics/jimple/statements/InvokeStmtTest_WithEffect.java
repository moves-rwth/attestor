package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.StaticInvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class InvokeStmtTest_WithEffect {
	
	private DefaultProgramState testInput;
	private HeapConfiguration expectedHeap;
	private InvokeStmt stmt;


	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception {
		testInput = new DefaultProgramState( ExampleHcImplFactory.getInput_InvokeWithEffect() );
		testInput.prepareHeap();
		
		DefaultProgramState expectedState = new DefaultProgramState( ExampleHcImplFactory.getExpectedResult_InvokeWithEffect() );
		expectedState.prepareHeap();
		expectedHeap = expectedState.getHeap();
		
		Type type = TypeFactory.getInstance().getType("List");
		Local varX = new Local(type, "x");
		Local varY = new Local(type, "y");
		Field nextOfX = new Field(type, varX, "next");
		Field nextOfY = new Field(type, varY, "next");
		
		AbstractMethod method = new AbstractMethod("method", StateSpaceFactoryHelper.get());
		List<Semantics> methodBody = new ArrayList<>();
		methodBody.add( new IdentityStmt(1, varY, "@parameter0:"));
		
		HashSet<String> liveVariables = new HashSet<>();	
		methodBody.add( new AssignStmt(nextOfY, varY, 2, liveVariables, false));
		methodBody.add( new ReturnValueStmt(varY, type) );
		method.setControlFlow( methodBody );
		
		StaticInvokeHelper invokeHelper = new StaticInvokeHelper(SingleElementUtil.createList(nextOfX),
				SingleElementUtil.createList("y"), false);
		stmt = new InvokeStmt(method, invokeHelper, 1);
		
	}

	@Test
	public void testComputeSuccessors() {
		try {
			Set<ProgramState> resStates = stmt.computeSuccessors( testInput );
			assertEquals( 1, resStates.size() );
			assertEquals( expectedHeap, resStates.iterator().next().getHeap() );
		} catch (NotSufficientlyMaterializedException e) {
			e.printStackTrace();
			fail( "unexpected exception");
		}
	}

	@Test
	public void testNeedsMaterialization() {

		assertTrue( stmt.needsMaterialization(testInput) );
	}

}
