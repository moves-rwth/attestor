package de.rwth.i2.attestor.grammar.materialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.*;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.DefaultIndexMaterialization;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;

public class GeneralMaterializationStrategyTest_Materialize_Indexed_OldExamples {

	private GeneralMaterializationStrategy materializer;

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	@Before
	public void setUp() throws Exception {
		Grammar balancedTreeGrammar = BalancedTreeGrammar.getGrammar();
		ViolationPointResolver vioResolver = new ViolationPointResolver(balancedTreeGrammar);
		
		IndexMatcher stackMatcher = new IndexMatcher( new DefaultIndexMaterialization() );
		MaterializationRuleManager ruleManager = 
				new IndexedMaterializationRuleManager(vioResolver, stackMatcher);
		
		GrammarResponseApplier ruleApplier = 
				new IndexedGrammarResponseApplier( new IndexMaterializationStrategy(), new GraphMaterializer() );
		this.materializer = new GeneralMaterializationStrategy( ruleManager, ruleApplier );
	}

	@Test
	public void testMaterialize_small_Z() {
		
		HeapConfiguration inputGraph 
				= ExampleIndexedGraphFactory.getInput_MaterializeSmall_Z();
		IndexedState inputState = new IndexedState( inputGraph );
		inputState.prepareHeap();
		
		HeapConfiguration expectedGraph
				= ExampleIndexedGraphFactory.getExpected_MaterializeSmall_Z();
		IndexedState expectedState = new IndexedState( expectedGraph );
		expectedState.prepareHeap();
		
		ViolationPoints vioPoints = new ViolationPoints();
		vioPoints.add("x", "left");
		vioPoints.add("x", "right");
		
		List<ProgramState> materializedStates = materializer.materialize( inputState, vioPoints );
		
		assertEquals( 1, materializedStates.size() );
		assertEquals( expectedState, materializedStates.get(0) );
	}
	
	@Test
	public void testMaterialize_small_sZ() {
		HeapConfiguration inputGraph 
				= ExampleIndexedGraphFactory.getInput_MaterializeSmall_sZ();
		IndexedState inputState = new IndexedState(inputGraph);
		inputState.prepareHeap();
		
		ViolationPoints vioPoints = new ViolationPoints();
		vioPoints.add("x", "left");
		vioPoints.add("x", "right");
		
		List<ProgramState> materializedStates = materializer.materialize( inputState, vioPoints );
		//assertEquals( 3, materializedStates.size() );
		
		
		IndexedState res1 = new IndexedState( ExampleIndexedGraphFactory.getExpected_MaterializeSmall2_Res1() );
		res1.prepareHeap();
		IndexedState res2 = new IndexedState( ExampleIndexedGraphFactory.getExpected_MaterializeSmall2_Res2() );
		res2.prepareHeap();
		IndexedState res3 = new IndexedState( ExampleIndexedGraphFactory.getExpected_MaterializeSmall2_Res3() );
		res3.prepareHeap();		
		
		assertTrue("should contain res1", materializedStates.contains(res1) );
		assertTrue("should contain res2", materializedStates.contains(res2) );
		//res3.equals( materializedStates.get(2) );
		assertTrue("should contain res3", materializedStates.contains(res3) );
	}
	


}
