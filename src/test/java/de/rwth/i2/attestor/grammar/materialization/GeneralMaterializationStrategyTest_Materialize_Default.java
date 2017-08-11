package de.rwth.i2.attestor.grammar.materialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.GeneralNonterminal;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;

public class GeneralMaterializationStrategyTest_Materialize_Default {

	private static GeneralMaterializationStrategy materializer;
	
	@BeforeClass
	public static void setUp() throws Exception {

		UnitTestGlobalSettings.reset();

		GeneralNonterminal listLabel = GeneralNonterminal
				.getNonterminal( "List", 2, new boolean[] { false, true } );
		
		Grammar grammar = Grammar.builder()
								.addRule( listLabel , ExampleHcImplFactory.getListRule1() )
								.addRule( listLabel , ExampleHcImplFactory.getListRule2() )
								.build();
		
		ViolationPointResolver violationPointResolver = new ViolationPointResolver(grammar);
		MaterializationRuleManager ruleManager = 
				new DefaultMaterializationRuleManager(violationPointResolver);
		GraphMaterializer graphMaterializer = new GraphMaterializer();
		GrammarResponseApplier ruleApplier = new DefaultGrammarResponseApplier(graphMaterializer);
		
		materializer = new GeneralMaterializationStrategy( ruleManager, ruleApplier );
		
	}

	@Test
	public void testMaterialize_Default() {
		
		HeapConfiguration testInput = ExampleHcImplFactory.getMaterializationTest();
		DefaultProgramState inputConf = new DefaultProgramState(testInput);
		
		ViolationPoints vio = new ViolationPoints("x", "next");
		
		List<ProgramState> res = materializer.materialize(inputConf, vio);
		
		assertEquals("input graph should not change", ExampleHcImplFactory.getMaterializationTest(), testInput );
		assertEquals( 2, res.size() );
		
		for(int i=0; i < 2; i++) {
			
			HeapConfiguration hc = res.get(i).getHeap();
			int x = hc.variableWith("0-x");
			int t = hc.targetOf(x);
			
			
			assertTrue(hc.selectorLabelsOf(t).contains(GeneralSelectorLabel.getSelectorLabel("next")));
		}
		
		List<HeapConfiguration> resHCs = new ArrayList<>();
		resHCs.add( res.get(0).getHeap() );
		resHCs.add( res.get(1).getHeap() );
		
		assertTrue("first expected materialization", resHCs.contains( ExampleHcImplFactory.getMaterializationRes1() ) );
		assertTrue("second expected materialization", resHCs.contains( ExampleHcImplFactory.getMaterializationRes2() ) );
	}

}
