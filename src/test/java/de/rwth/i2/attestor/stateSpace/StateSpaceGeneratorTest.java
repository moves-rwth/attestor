package de.rwth.i2.attestor.stateSpace;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupAbortStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupCanonicalizationStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupMaterializationStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupStateLabellingStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.IntConstant;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NewExpr;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.tasks.GeneralInclusionStrategy;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StateSpaceGeneratorTest {
	//private static final Logger logger = LogManager.getLogger( "StateSpaceGeneratorTest" );

	private SSGBuilder ssgBuilder;

	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
	}


	@Before
	public void setup() {

	    ssgBuilder = StateSpaceGenerator.builder()
                .setStateLabelingStrategy(new MockupStateLabellingStrategy())
                .setAbortStrategy(new MockupAbortStrategy())
                .setCanonizationStrategy(new MockupCanonicalizationStrategy())
                .setMaterializationStrategy(new MockupMaterializationStrategy())
                .setStateRefinementStrategy(s -> s)
                .setInclusionStrategy(new GeneralInclusionStrategy());
	}

	@Test
	public void testGenerate1() {
	
		HeapConfiguration initialGraph = ExampleHcImplFactory.getEmptyGraphWithConstants();
		
		List<Semantics> programInstructions = new ArrayList<>();
		programInstructions.add( new Skip( 1 ) );
		programInstructions.add( new ReturnVoidStmt() );
		Program mainProgram = new Program( programInstructions );


		ProgramState initialState = new DefaultState(initialGraph);
		StateSpace res = ssgBuilder
                .setProgram(mainProgram)
                .addInitialState(initialState)
                .build()
                .generate();

		assertEquals( 3, res.getStates().size() );
		assertEquals( 1, res.getFinalStates().size() );
		assertEquals( initialGraph,  res.getFinalStates().get( 0 ).getHeap() );
	}

	@Test
	public void testGenerateNew() {		
		HeapConfiguration initialGraph 
				= ExampleHcImplFactory.getEmptyGraphWithConstants();
		
		Type type = TypeFactory.getInstance().getType( "type" );
		
		List<Semantics> programInstructions = new ArrayList<>();
		Statement skipStmt = new Skip( 1 );
		programInstructions.add( skipStmt );
		Statement assignStmt = new AssignStmt( new Local( type, "x" ), new NewExpr( type ),
                2, new HashSet<>(), false );
		programInstructions.add( assignStmt );
		Statement returnStmt = new ReturnVoidStmt();
		programInstructions.add( returnStmt );

		Program mainProgram = new Program( programInstructions );

		ProgramState initialState = new DefaultState(initialGraph);
        StateSpace res = ssgBuilder
                .setProgram(mainProgram)
                .addInitialState(initialState)
                .build()
                .generate();

		assertEquals( 4, res.getStates().size() );
		assertEquals( 1, res.getFinalStates().size() );
		assertFalse( initialGraph.equals( res.getFinalStates().get( 0 ).getHeap() ) );
		HeapConfiguration expectedState = ExampleHcImplFactory.getExpectedResultTestGenerateNew();
		assertEquals(expectedState, res.getFinalStates().get(0).getHeap());
		DefaultState firstState = (DefaultState)  res.getStates().get( 0 );
		assertEquals( skipStmt.toString() , res.getSuccessors().get( firstState ).get( 0 ).getLabel() );
		DefaultState secondState = (DefaultState) res.getStates().get( 1 );
		assertEquals( assignStmt.toString(), res.getSuccessors().get( secondState ).get( 0 ).getLabel() );
		DefaultState thirdState = (DefaultState) res.getStates().get( 2 );
		assertEquals( returnStmt.toString(), res.getSuccessors().get( thirdState ).get( 0 ).getLabel() );
		DefaultState fourthState = (DefaultState) res.getStates().get( 3 );
		assertFalse( res.getSuccessors().containsKey( fourthState ) );
	}
	
	@Test
	public void testGenerateIf() {
		
		HeapConfiguration initialGraph = ExampleHcImplFactory.getEmptyGraphWithConstants();
		
		List<Semantics> programInstructions = new ArrayList<>();
		Statement ifStmt = new IfStmt( new IntConstant( 1 ), 1, 2, new HashSet<>(), false );
		programInstructions.add( ifStmt );
		Statement firstReturn = new ReturnVoidStmt();
		programInstructions.add( firstReturn );
		Statement secondReturn = new ReturnValueStmt( new IntConstant( 0 ), null );
		programInstructions.add( secondReturn );
		Program mainProgram = new Program( programInstructions );

        ProgramState initialState = new DefaultState(initialGraph);
        StateSpace res = ssgBuilder
                .setProgram(mainProgram)
                .addInitialState(initialState)
                .build()
                .generate();

		assertEquals( 3, res.getStates().size() );
		assertEquals( 1, res.getFinalStates().size() );
		assertEquals( initialGraph,  res.getFinalStates().get( 0 ).getHeap()  );
		DefaultState firstState = (DefaultState) res.getStates().get( 0 );
		assertEquals( ifStmt.toString(), res.getSuccessors().get( firstState ).get( 0 ).getLabel() );
		DefaultState secondState = (DefaultState) res.getStates().get( 1 );
		assertFalse( secondReturn.toString().equals( res.getSuccessors().get( secondState ).get( 0 ).getLabel() ) );
		assertEquals( firstReturn.toString(), res.getSuccessors().get( secondState ).get( 0 ).getLabel() );
		DefaultState thirdState = (DefaultState) res.getStates().get( 2 );
		assertFalse( res.getSuccessors().containsKey( thirdState ) );
	}
}
