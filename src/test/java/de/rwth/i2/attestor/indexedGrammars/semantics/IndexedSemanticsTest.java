package de.rwth.i2.attestor.indexedGrammars.semantics;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.indexedGrammars.ExampleIndexedGraphFactory;
import de.rwth.i2.attestor.indexedGrammars.IndexedState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.AssignStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NewExpr;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IndexedSemanticsTest {

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Test
	public void testFieldAssign() {
		IndexedState input = new IndexedState( ExampleIndexedGraphFactory.getInput_FieldAccess() );
		input.prepareHeap();
		IndexedState expected = new IndexedState( ExampleIndexedGraphFactory.getExpected_FieldAccess() );
		expected.prepareHeap();
		
		Type type = TypeFactory.getInstance().getType("AVLTree");
		
		Local varX = new Local(type, "x");
		Field xLeft = new Field(type, varX, "left");
		AssignStmt stmt = new AssignStmt(varX, xLeft, 0, new HashSet<>(), false);
		try {
			Set<ProgramState> result = stmt.computeSuccessors(input);
			assertEquals(1, result.size());
			assertEquals(expected, result.iterator().next());
		} catch (NotSufficientlyMaterializedException e) {
			e.printStackTrace();
			fail("Unexpected Exception " + e.getMessage() );
		}
		
	}
	
	@Test
	public void testNew(){
		IndexedState input = new IndexedState(ExampleIndexedGraphFactory.getExpected_FieldAccess());
		input.prepareHeap();
		IndexedState expected = new IndexedState(ExampleIndexedGraphFactory.getExpected_newNode() );
		expected.prepareHeap();
		
		Type type = TypeFactory.getInstance().getType("AVLTree");
		
		Local varTmp = new Local(type, "tmp");
		NewExpr expr = new NewExpr(type);
		AssignStmt stmt = new AssignStmt(varTmp, expr, 0, new HashSet<>(), false);
		try {
			Set<ProgramState> result = stmt.computeSuccessors(input);
			assertEquals(1, result.size());
			assertEquals(expected, result.iterator().next());
		} catch (NotSufficientlyMaterializedException e) {
			e.printStackTrace();
			fail("Unexpected Exception " + e.getMessage() );
		}
	}

	@Test
	public void testAssignField(){
		IndexedState input = new IndexedState(ExampleIndexedGraphFactory.getExpected_newNode() );
		input.prepareHeap();
		IndexedState expected = new IndexedState( ExampleIndexedGraphFactory.getExpected_fieldAssign() );
		expected.prepareHeap();
		
		Type type = TypeFactory.getInstance().getType("AVLTree");
		Local varTmp = new Local(type, "tmp");
		Local varX = new Local(type, "x");
		Field xLeft = new Field(type, varX, "left");
		AssignStmt stmt = new AssignStmt(xLeft, varTmp, 0, new HashSet<>(), false);
		
		try {
			Set<ProgramState> result = stmt.computeSuccessors(input);
			assertEquals(1, result.size());
			assertEquals(expected, result.iterator().next());
		} catch (NotSufficientlyMaterializedException e) {
			e.printStackTrace();
			fail("Unexpected Exception " + e.getMessage() );
		}
	}
}
