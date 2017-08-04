package de.rwth.i2.attestor.materialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.GeneralNonterminal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultState;

public class MaterializationTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "MaterializationTest" );

	private static GeneralMaterializationStrategy materializer;
	

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
		
		GeneralNonterminal listLabel = GeneralNonterminal
				.getNonterminal( "List", 2, new boolean[] { false, true } );
		
		Grammar grammar = Grammar.builder()
				.addRule( listLabel , ExampleHcImplFactory.getListRule1())
				.addRule( listLabel , ExampleHcImplFactory.getListRule2())
				.build();
		
		ViolationPointResolver vioResolver = new ViolationPointResolver(grammar);
		MaterializationRuleManager grammarManager = 
				new DefaultMaterializationRuleManager(vioResolver);
		
		GrammarResponseApplier ruleApplier = 
				new DefaultGrammarResponseApplier( new GraphMaterializer() );
		
		materializer = new GeneralMaterializationStrategy( grammarManager, ruleApplier );
	}
	
	
	@Before
	public void setUp() throws Exception {

	}
	
	@Test
	public void testMaterialization() {
		
		HeapConfiguration testInput = ExampleHcImplFactory.getMaterializationTest();
		DefaultState inputConf = new DefaultState(testInput);
		
		ViolationPoints vio = new ViolationPoints("x", "next");
		
		List<ProgramState> res = materializer.materialize(inputConf, vio);
		
		assertEquals("input graph should not change", ExampleHcImplFactory.getMaterializationTest(), testInput );
		assertEquals(2, res.size());
		
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
