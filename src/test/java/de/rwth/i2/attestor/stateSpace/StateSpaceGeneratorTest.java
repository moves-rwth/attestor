package de.rwth.i2.attestor.stateSpace;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupAbortStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupCanonicalizationStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupMaterializationStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls.MockupStateLabellingStrategy;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.IntConstant;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NewExpr;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

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
				.setStateCounter(s -> {})
				.setExplorationStrategy((s,sp) -> true)
				.setStateSpaceSupplier(() -> new InternalStateSpace(100))
				.setSemanticsOptionsSupplier(s -> new MockupSemanticsOptions())
				;
	}

	@Test
	public void testGenerate1() {
	
		HeapConfiguration initialGraph = ExampleHcImplFactory.getEmptyGraphWithConstants();
		
		List<Semantics> programInstructions = new ArrayList<>();
		programInstructions.add( new Skip( 1 ) );
		programInstructions.add( new ReturnVoidStmt() );
		Program mainProgram = new Program( programInstructions );


		ProgramState initialState = new DefaultProgramState(initialGraph);
		StateSpace res = null;
		try {
			res = ssgBuilder
                    .setProgram(mainProgram)
                    .addInitialState(initialState)
                    .build()
                    .generate();
		} catch (StateSpaceGenerationAbortedException e) {
			fail("State space generation aborted");
		}

		assertEquals( 3, res.getStates().size() );
		assertEquals( 1, res.getFinalStates().size() );
		assertEquals( initialGraph,  res.getFinalStates().iterator().next().getHeap() );
	}

	@Test
	public void testGenerateNew() {		
		HeapConfiguration initialGraph 
				= ExampleHcImplFactory.getEmptyGraphWithConstants();
		
		Type type = Settings.getInstance().factory().getType( "type" );
		
		List<Semantics> programInstructions = new ArrayList<>();
		Statement skipStmt = new Skip( 1 );
		programInstructions.add( skipStmt );
		Statement assignStmt = new AssignStmt( new Local( type, "x" ), new NewExpr( type ),
                2, new HashSet<>());
		programInstructions.add( assignStmt );
		Statement returnStmt = new ReturnVoidStmt();
		programInstructions.add( returnStmt );

		Program mainProgram = new Program( programInstructions );

		ProgramState initialState = new DefaultProgramState(initialGraph);
		StateSpace res = null;
		try {
			res = ssgBuilder
                    .setProgram(mainProgram)
                    .addInitialState(initialState)
                    .build()
                    .generate();
		} catch (StateSpaceGenerationAbortedException e) {
			fail("State space generation aborted");
		}

		assertEquals( 4, res.getStates().size() );
		assertEquals( 1, res.getFinalStates().size() );
		assertFalse( initialGraph.equals( res.getFinalStates().iterator().next().getHeap() ) );
		HeapConfiguration expectedState = ExampleHcImplFactory.getExpectedResultTestGenerateNew();
		assertEquals(expectedState, res.getFinalStates().iterator().next().getHeap());

		for(ProgramState state : res.getStates()) {

			int controlSuccSize = res.getControlFlowSuccessorsOf(state).size();
			int materSuccSize = res.getMaterializationSuccessorsOf(state).size();

			switch(state.getProgramCounter()) {
				case 0:
					assertEquals(1, controlSuccSize);
					assertEquals(0, materSuccSize);
					assertFalse(res.getControlFlowSuccessorsOf(state).contains(state));
					break;
				case 1:
					assertEquals(1, controlSuccSize);
					assertEquals(0, materSuccSize);
					break;
				case 2:
					assertEquals(1, controlSuccSize);
					assertEquals(0, materSuccSize);
					break;
				case -1:
					assertEquals(0, controlSuccSize);
					assertEquals(0, materSuccSize);
					break;
				default:
					fail("Unknown state with PC " + state.getProgramCounter());
			}
		}
	}
	
	@Test
	public void testGenerateIf() {
		
		HeapConfiguration initialGraph = ExampleHcImplFactory.getEmptyGraphWithConstants();
		
		List<Semantics> programInstructions = new ArrayList<>();
		Statement ifStmt = new IfStmt( new IntConstant( 1 ), 1, 2, new HashSet<>());
		programInstructions.add( ifStmt );
		Statement firstReturn = new ReturnVoidStmt();
		programInstructions.add( firstReturn );
		Statement secondReturn = new ReturnValueStmt( new IntConstant( 0 ), null );
		programInstructions.add( secondReturn );
		Program mainProgram = new Program( programInstructions );

        ProgramState initialState = new DefaultProgramState(initialGraph);
		StateSpace res = null;
		try {
			res = ssgBuilder
                    .setProgram(mainProgram)
                    .addInitialState(initialState)
                    .build()
                    .generate();
		} catch (StateSpaceGenerationAbortedException e) {
			fail("State space generation aborted");
		}

		assertEquals( 3, res.getStates().size() );
		assertEquals( 1, res.getFinalStates().size() );
		assertEquals( initialGraph,  res.getFinalStates().iterator().next().getHeap()  );

		for(ProgramState state : res.getStates()) {

			int controlSuccSize = res.getControlFlowSuccessorsOf(state).size();
			int materSuccSize = res.getMaterializationSuccessorsOf(state).size();

			switch(state.getProgramCounter()) {
				case 0:
					assertEquals(1, controlSuccSize);
					assertEquals(0, materSuccSize);
					assertFalse(res.getControlFlowSuccessorsOf(state).contains(state));
					break;
				case 1:
					assertEquals(1, controlSuccSize);
					assertEquals(0, materSuccSize);
					break;
				case -1:
					assertEquals(0, controlSuccSize);
					assertEquals(0, materSuccSize);
					break;
				default:
					fail("Unknown state with PC " + state.getProgramCounter());
			}
		}
	}
}
