package de.rwth.i2.attestor.grammar.materialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.main.environment.SceneObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;

public class GeneralMaterializationStrategyTest_Materialize_Default {

	private GeneralMaterializationStrategy materializer;

	private SceneObject sceneObject;
	private ExampleHcImplFactory hcFactory;

	@BeforeClass
	public static void setUpClass() throws Exception {

		UnitTestGlobalSettings.reset();

	}

	@Before
	public void setUp() {
		sceneObject = new MockupSceneObject();
		hcFactory = new ExampleHcImplFactory(sceneObject);

		BasicNonterminal listLabel = BasicNonterminal
				.getNonterminal( "List", 2, new boolean[] { false, true } );

		Grammar grammar = Grammar.builder()
				.addRule( listLabel , hcFactory.getListRule1() )
				.addRule( listLabel , hcFactory.getListRule2() )
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
		
		HeapConfiguration testInput = hcFactory.getMaterializationTest();
		DefaultProgramState inputConf = new DefaultProgramState(testInput);
		
		ViolationPoints vio = new ViolationPoints("x", "next");
		
		List<ProgramState> res = materializer.materialize(inputConf, vio);
		
		assertEquals("input graph should not change", hcFactory.getMaterializationTest(), testInput );
		assertEquals( 2, res.size() );
		
		for(int i=0; i < 2; i++) {
			
			HeapConfiguration hc = res.get(i).getHeap();
			int x = hc.variableWith("x");
			int t = hc.targetOf(x);
			
			
			assertTrue(hc.selectorLabelsOf(t).contains(sceneObject.scene().getSelectorLabel("next")));
		}
		
		List<HeapConfiguration> resHCs = new ArrayList<>();
		resHCs.add( res.get(0).getHeap() );
		resHCs.add( res.get(1).getHeap() );
		
		assertTrue("first expected materialization", resHCs.contains( hcFactory.getMaterializationRes1() ) );
		assertTrue("second expected materialization", resHCs.contains( hcFactory.getMaterializationRes2() ) );
	}

}
