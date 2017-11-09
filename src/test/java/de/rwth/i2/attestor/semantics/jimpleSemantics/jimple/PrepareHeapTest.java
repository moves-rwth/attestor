package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.IfStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.MockupSemanticsObserver;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullConstant;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.boolExpr.EqualExpr;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PrepareHeapTest {
	//private static final Logger logger = LogManager.getLogger( "PrepareHeapTest.java" );

	private HeapConfiguration testGraph;
	private int truePC;
	private int falsePC;
	private Type listType;

	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception{
		testGraph = ExampleHcImplFactory.getList();
		listType = Settings.getInstance().factory().getType( "List" );

		truePC = 5;
		falsePC = 7;
	}

	@Test
	public void testWithLocal(){

		Value leftExpr = new Local( listType, "x" );
		Value rightExpr = new NullConstant();
		Value condition = new EqualExpr( leftExpr, rightExpr );
		Statement stmt = new IfStmt( condition, truePC, falsePC, new HashSet<>());

		try{
			DefaultProgramState input = new DefaultProgramState( testGraph );
			input.prepareHeap();
			Set<ProgramState> res = stmt.computeSuccessors( input, new MockupSemanticsObserver());
			
			assertEquals( "result should have size 1", 1, res.size() );
			
			
			for(ProgramState s : res) {
				assertTrue( "condition should evaluate to false", s.getProgramCounter() == falsePC );
				assertFalse( "condition has evaluated to true", s.getProgramCounter() ==  truePC );	
				assertNotNull( "resHeap null", s.getHeap() );
			}

		}catch( NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e ){
			fail( "Unexpected Exception: " + e.getMessage() );
		}

	}

	@Test
	public void testWithField(){
		Value origin = new Local( listType, "x" );
		Value leftExpr = new Field( listType, origin, "next" );
		Value rightExpr = new NullConstant();
		Value condition = new EqualExpr( leftExpr, rightExpr );
		Statement stmt = new IfStmt( condition, truePC, falsePC, new HashSet<>());

		try{
			DefaultProgramState input = new DefaultProgramState( testGraph );
			input.prepareHeap();
			Set<ProgramState> res = stmt.computeSuccessors( input, new MockupSemanticsObserver() );

			assertEquals( "result should have size 1", 1, res.size() );
			
			for(ProgramState s : res) {
				assertTrue( "condition should evaluate to false", s.getProgramCounter() == falsePC );
				assertFalse( "condition has evaluated to true", s.getProgramCounter() == truePC );	
				assertNotNull( "resHeap null", s.getHeap() );
			}

		}catch( NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e ){
			fail( "Unexpected Exception: " + e.getMessage() );
		}
	}

	@Test
	public void testToTrue(){

		Value origin1 = new Local( listType, "x" );
		Value origin2 = new Field( listType, origin1, "next" );
		Value origin3 = new Field( listType, origin2, "next" );
		Value leftExpr = new Field( listType, origin3, "next" );
		Value rightExpr = new NullConstant();
		Value condition = new EqualExpr( leftExpr, rightExpr );
		Statement stmt = new IfStmt( condition, truePC, falsePC, new HashSet<>());

		try{
			DefaultProgramState input = new DefaultProgramState( testGraph );
			input.prepareHeap();

			Set<ProgramState> res = stmt.computeSuccessors( input, new MockupSemanticsObserver() );

			assertEquals( "result should have size 1", 1, res.size() );
			
			for(ProgramState s : res) {
				assertFalse( "condition should evaluate to true, but got false", s.getProgramCounter() == falsePC );
				assertTrue( "condition should evaluate to true", s.getProgramCounter() == truePC );	
				assertNotNull( "resHeap null", s.getHeap() );
			}

		}catch( NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e ){
			fail( "Unexpected Exception: " + e.getMessage() );
		}
	}

}
