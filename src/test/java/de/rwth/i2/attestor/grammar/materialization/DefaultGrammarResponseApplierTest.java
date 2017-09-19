package de.rwth.i2.attestor.grammar.materialization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.*;

import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.materialization.communication.*;
import de.rwth.i2.attestor.graph.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;

public class DefaultGrammarResponseApplierTest {

	private static final int EDGE_ID = -1;
	private static final boolean[] REDUCTION_TENTACLES = new boolean [] {true,false};
	private static final String UNIQUE_NT_LABEL = "DefaultGrammarResponseApplier";
	private static final int RANK = 2;

	@BeforeClass
	public static void init() {
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDelegationWithOneRule() throws WrongResponseTypeException {
		GraphMaterializer graphMaterializerMock = mock(GraphMaterializer.class);
		GrammarResponseApplier ruleApplier = 
				new DefaultGrammarResponseApplier( graphMaterializerMock ); 
		
		HeapConfiguration inputGraph = getInputGraph();
		
		HeapConfiguration rule1 = createSimpleRule();
		Set<HeapConfiguration> rulesInResponse = SingleElementUtil.createSet( rule1 );
		GrammarResponse grammarResponse = new DefaultGrammarResponse( rulesInResponse );
		
		ruleApplier.applyGrammarResponseTo( inputGraph, EDGE_ID, grammarResponse );
		verify( graphMaterializerMock ).getMaterializedCloneWith( inputGraph, EDGE_ID, rule1 );
	}
	
	@Test
	public void testDelegationWithMoreRules() throws WrongResponseTypeException{
		GraphMaterializer graphMaterializerMock = mock(GraphMaterializer.class);
		GrammarResponseApplier ruleApplier = 
				new DefaultGrammarResponseApplier( graphMaterializerMock ); 
		
		HeapConfiguration inputGraph = getInputGraph();
		
		HeapConfiguration rule1 = createSimpleRule();
		HeapConfiguration rule2 = createBigRule();
		HeapConfiguration rule3 = createOtherBigRule();
		Set<HeapConfiguration> rulesInResponse = new HashSet<>();
		rulesInResponse.add( rule1 );
		rulesInResponse.add( rule2 );
		rulesInResponse.add( rule3 );
		GrammarResponse grammarResponse = new DefaultGrammarResponse( rulesInResponse );
		ruleApplier.applyGrammarResponseTo( inputGraph, EDGE_ID, grammarResponse );
		verify( graphMaterializerMock ).getMaterializedCloneWith( inputGraph, EDGE_ID, rule1 );
		verify( graphMaterializerMock ).getMaterializedCloneWith( inputGraph, EDGE_ID, rule2 );
		verify( graphMaterializerMock ).getMaterializedCloneWith( inputGraph, EDGE_ID, rule3 );
		
	}

	@Test
	public void integrationTest_OneRule() throws WrongResponseTypeException{
		
		GraphMaterializer graphMaterializer = new GraphMaterializer();
		GrammarResponseApplier ruleApplier = 
				new DefaultGrammarResponseApplier(graphMaterializer);
		
		HeapConfiguration inputGraph = getInputGraph();
		
		HeapConfiguration rule1 = createSimpleRule();
		Set<HeapConfiguration> rulesInResponse = SingleElementUtil.createSet( rule1 );
		
		GrammarResponse grammarResponse = new DefaultGrammarResponse( rulesInResponse );
		
		int edge_id = inputGraph.nonterminalEdges().get(0);
		Collection<HeapConfiguration> materializedGraphs = 
				ruleApplier.applyGrammarResponseTo( inputGraph, edge_id, grammarResponse );
		
		assertThat( materializedGraphs, containsInAnyOrder( expectedResult_ApplySimpleRule() ) );
		assertEquals( getInputGraph(), inputGraph );
		assertEquals( createSimpleRule(), rule1 );
	}
	
	@Test
	public void integrationTest_MultipleRules() throws WrongResponseTypeException{
		
		GraphMaterializer graphMaterializer = new GraphMaterializer();
		GrammarResponseApplier ruleApplier = 
				new DefaultGrammarResponseApplier(graphMaterializer);
		
		HeapConfiguration inputGraph = getInputGraph();
		
		HeapConfiguration simpleRule = createSimpleRule();
		HeapConfiguration bigRule = createBigRule();
		HeapConfiguration otherBigRule = createOtherBigRule();
		Set<HeapConfiguration> rulesInResponse = new HashSet<>();
		rulesInResponse.add(simpleRule);
		rulesInResponse.add(bigRule);
		rulesInResponse.add(otherBigRule);
		
		GrammarResponse grammarResponse = new DefaultGrammarResponse( rulesInResponse );
		
		int edge_id = inputGraph.nonterminalEdges().get(0);
		Collection<HeapConfiguration> materializedGraphs = 
				ruleApplier.applyGrammarResponseTo( inputGraph, edge_id, grammarResponse );
		
		assertThat( materializedGraphs, containsInAnyOrder( expectedResult_ApplySimpleRule(), 
														    expectedResult_applyBigRule(),
														    expectedResult_applyOtherBigRule() 
														    ) );
		assertEquals( getInputGraph(), inputGraph );
		assertEquals( createSimpleRule(), simpleRule );
		assertEquals( createBigRule(), bigRule );
		assertEquals( createOtherBigRule(), otherBigRule );
	}

	private HeapConfiguration getInputGraph() {
		InternalHeapConfiguration hc = new InternalHeapConfiguration();
		Type type = Settings.getInstance().factory().getType("type");
		Nonterminal nt = BasicNonterminal.getNonterminal( UNIQUE_NT_LABEL,
															RANK, 
															REDUCTION_TENTACLES );

		TIntArrayList nodes = new TIntArrayList();
		
		return  hc.builder()
				.addNodes(type, 2, nodes)
				.addNonterminalEdge(nt, nodes ) 
				.build();
	}

	private HeapConfiguration createSimpleRule() {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("type");
		SelectorLabel sel = BasicSelectorLabel.getSelectorLabel("someSelectorLabel");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(1) )
				.addSelector(nodes.get(0), sel, nodes.get(1) )
				.build();
	}
	
	private HeapConfiguration expectedResult_ApplySimpleRule(){
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("type");
		SelectorLabel sel = BasicSelectorLabel.getSelectorLabel("someSelectorLabel");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addSelector(nodes.get(0), sel, nodes.get(1) )
				.build();
	}
		

	private HeapConfiguration createBigRule() {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("type");
		Nonterminal nt = BasicNonterminal.getNonterminal( UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES );
		SelectorLabel sel = BasicSelectorLabel.getSelectorLabel("someSelectorLabel");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 3, nodes)
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(2) )
				.addNonterminalEdge(nt)
					.addTentacle( nodes.get(0) )
					.addTentacle( nodes.get(1) )
					.build()
				.addSelector(nodes.get(1), sel, nodes.get(2) )
				.build();
	}
	
	private HeapConfiguration expectedResult_applyBigRule(){
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("type");
		Nonterminal nt = BasicNonterminal.getNonterminal( UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES );
		SelectorLabel sel = BasicSelectorLabel.getSelectorLabel("someSelectorLabel");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 3, nodes)
				.addNonterminalEdge(nt)
					.addTentacle( nodes.get(0) )
					.addTentacle( nodes.get(1) )
					.build()
				.addSelector(nodes.get(1), sel, nodes.get(2) )
				.build();
	}
	
	private HeapConfiguration createOtherBigRule() {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("type");
		Nonterminal nt = BasicNonterminal.getNonterminal( UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES );
		SelectorLabel sel = BasicSelectorLabel.getSelectorLabel("someSelectorLabel");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 3, nodes)
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(2) )
				.addSelector(nodes.get(0), sel, nodes.get(1) )
				.addNonterminalEdge(nt)
					.addTentacle( nodes.get(1) )
					.addTentacle( nodes.get(2) )
					.build()
				.build();
	}
	
	private HeapConfiguration expectedResult_applyOtherBigRule(){
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("type");
		Nonterminal nt = BasicNonterminal.getNonterminal( UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES );
		SelectorLabel sel = BasicSelectorLabel.getSelectorLabel("someSelectorLabel");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), sel, nodes.get(1) )
				.addNonterminalEdge(nt)
					.addTentacle( nodes.get(1) )
					.addTentacle( nodes.get(2) )
					.build()
				.build();
	}
}