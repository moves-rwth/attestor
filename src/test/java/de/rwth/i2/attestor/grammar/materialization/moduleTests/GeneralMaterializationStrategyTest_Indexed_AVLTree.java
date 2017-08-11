package de.rwth.i2.attestor.grammar.materialization.moduleTests;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.BalancedTreeGrammar;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class GeneralMaterializationStrategyTest_Indexed_AVLTree {

	private static final AbstractIndexSymbol ABSTRACT_STACK_SYMBOL = AbstractIndexSymbol.get("X");
	private static final ConcreteIndexSymbol STACK_SYMBOL_Z = ConcreteIndexSymbol.getStackSymbol("Z", true);
	private static final ConcreteIndexSymbol STACK_SYMBOL_S = ConcreteIndexSymbol.getStackSymbol("s", false);
	private static final String VIOLATIONPOINT_VARIABLE = "x";
	private GeneralMaterializationStrategy materializer;

	@BeforeClass
	public static void init() {
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() throws Exception {
		Grammar balancedTreeGrammar = BalancedTreeGrammar.getGrammar();
		ViolationPointResolver vioResolver = new ViolationPointResolver(balancedTreeGrammar);
		
		IndexMatcher indexMatcher = new IndexMatcher( new DefaultIndexMaterialization() );
		MaterializationRuleManager ruleManager = 
				new IndexedMaterializationRuleManager(vioResolver, indexMatcher);
		
		GrammarResponseApplier ruleApplier = 
				new IndexedGrammarResponseApplier( new IndexMaterializationStrategy(), new GraphMaterializer() );
		this.materializer = new GeneralMaterializationStrategy( ruleManager, ruleApplier );
	}

	/**
	 * Only one rule applicable. index of lhs of this rule exactly matches the
	 * index of the nonterminal in the heap.<br>
	 * Instantiation: No <br>
	 * Materialization: No
	 */
	@Test
	public void testMaterialize_ConcreteStack_OneRule(){
		List<IndexSymbol> stackForReferenceNt = getStack_sX();
		
		final HeapConfiguration inputHeap = 
				getInputWithStackZ( stackForReferenceNt );
		ProgramState inputState = new IndexedState( inputHeap ).prepareHeap();
		final HeapConfiguration expectedHeap = 
				getAppliedBalancedLeafRule_WithReferenceStack( stackForReferenceNt );
		ProgramState expectedState = new IndexedState( expectedHeap ).prepareHeap();
		
		ViolationPoints vioPoints = new ViolationPoints();
		vioPoints.add(VIOLATIONPOINT_VARIABLE, "left");
		
		List<ProgramState> materializedStates = materializer.materialize( inputState, vioPoints );
		
		assertThat( materializedStates, containsInAnyOrder( expectedState) );
	}
	
	/**
	 * Several Rules applicable. In two cases the index of the lhs exactly matches
	 * the nonterminal in the heap. In one case it is instantiated.<br>
	 * Instantiation: Yes <br>
	 * Materialization: No <br>
	 */
	@Test
	public void testMaterialize_ConcreteStack_MoreRules(){
		List<IndexSymbol> stackForReferenceNt = getStack_sZ();
		
		final HeapConfiguration inputHeap = 
				getInputWithStack_sZ( stackForReferenceNt );
		ProgramState inputState = new IndexedState( inputHeap ).prepareHeap();
		
		final HeapConfiguration expectedHeap1 = 
				getExpected_sZ_BalancedRule( stackForReferenceNt );
		ProgramState expectedState1 = new IndexedState( expectedHeap1 ).prepareHeap();
		final HeapConfiguration expectedHeap2 = 
				getAppliedLeftLeafRuleWithReferenceStack( stackForReferenceNt );
		ProgramState expectedState2 = new IndexedState( expectedHeap2 ).prepareHeap();
		final HeapConfiguration expectedHeap3 = 
				getAppliedRightLeafRuleWithReferenceStack( stackForReferenceNt );
		ProgramState expectedState3 = new IndexedState( expectedHeap3 ).prepareHeap();
		
		ViolationPoints vioPoints = new ViolationPoints();
		vioPoints.add(VIOLATIONPOINT_VARIABLE, "left");
		
		List<ProgramState> materializedStates = materializer.materialize( inputState, vioPoints );
		
		assertThat( materializedStates, containsInAnyOrder( expectedState1, 
															expectedState2,
															expectedState3) );
	}
	
	/**
	 * Several rules appliable. In each case, the rule needs instantiation, but
	 * the graph no materialization <br>
	 * Instantiation: yes <br>
	 * Materialization: no
	 */
	@Test
	public void testMaterialize_AbstractStack_OnlyInstantiation(){
		List<IndexSymbol> stackForReferenceNt = getStack_sX();
		
		final HeapConfiguration inputHeap = 
				getInputWithStack_ssX( stackForReferenceNt);
		ProgramState inputState = new IndexedState( inputHeap ).prepareHeap();
		
		final HeapConfiguration expectedHeap1 = 
				getExpectedOneNonterminal_ssX_BalancedRule( stackForReferenceNt );
		ProgramState expectedState1 = new IndexedState( expectedHeap1 ).prepareHeap();
		final HeapConfiguration expectedHeap2 = 
				getExpected_ssX_LeftRule( stackForReferenceNt );
		ProgramState expectedState2 = new IndexedState( expectedHeap2 ).prepareHeap();
		final HeapConfiguration expectedHeap3 = 
				getExpected_ssX_RightRule( stackForReferenceNt );
		ProgramState expectedState3 = new IndexedState( expectedHeap3 ).prepareHeap();
		
		ViolationPoints vioPoints = new ViolationPoints();
		vioPoints.add(VIOLATIONPOINT_VARIABLE, "left");
		
		List<ProgramState> materializedStates = materializer.materialize( inputState, vioPoints );
		
		assertThat( materializedStates, containsInAnyOrder( expectedState1, 
															expectedState2,
															expectedState3) );
	}
	
	/**
	 * Several rules appliable. In each case, the rule needs instantiation and the graph
	 * different materializations to match the different rules
	 * Instantiation: yes <br>
	 * Materialization: yes
	 */
	@Test
	public void testMaterialize_AbstractStack_WithMaterializationApplicable(){
		List<IndexSymbol> stackForReferenceNt = getStack_X();
		
		final HeapConfiguration inputHeap = 
				getInputWithStack_sX( stackForReferenceNt );
		ProgramState inputState = new IndexedState( inputHeap ).prepareHeap();
		
		final HeapConfiguration expectedHeap1 = 
				getExpected_sX_BalancedRule( getStack_X() );//no materialization 
		ProgramState expectedState1 = new IndexedState( expectedHeap1 ).prepareHeap();
		final HeapConfiguration expectedHeap2 = 
				getExpected_ssX_LeftRule( getStack_sX() );//materialization: X -> sX
		ProgramState expectedState2 = new IndexedState( expectedHeap2 ).prepareHeap();
		final HeapConfiguration expectedHeap3 = 
				getExpected_ssX_RightRule( getStack_sX() );//materialization X -> sX
		ProgramState expectedState3 = new IndexedState( expectedHeap3 ).prepareHeap();
		final HeapConfiguration expectedHeap4 = 
				getAppliedLeftLeafRuleWithReferenceStack(getStack_Z());//materialization X -> Z
		ProgramState expectedState4 = new IndexedState(expectedHeap4).prepareHeap();
		final HeapConfiguration expectedHeap5 = 
				getAppliedRightLeafRuleWithReferenceStack( getStack_Z() );//materialization X->Z
		ProgramState expectedState5 = new IndexedState(expectedHeap5).prepareHeap();
		
		ViolationPoints vioPoints = new ViolationPoints();
		vioPoints.add(VIOLATIONPOINT_VARIABLE, "left");
		
		List<ProgramState> materializedStates = materializer.materialize( inputState, vioPoints );
		
		assertThat( materializedStates, containsInAnyOrder( expectedState1, 
															expectedState2,
															expectedState3,
															expectedState4,
															expectedState5) );	
		}
	

	private HeapConfiguration getExpectedOneNonterminal_ssX_BalancedRule(List<IndexSymbol> stackForReferenceNt) {
		return getAppliedBalancedRuleWithStack( getStack_sX(), stackForReferenceNt );
	}

	

	private List<IndexSymbol> getStack_Z() {
		return SingleElementUtil.createList( STACK_SYMBOL_Z );
	}
	
	private List<IndexSymbol> getStack_sZ() {
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(STACK_SYMBOL_S);
		stack.add(STACK_SYMBOL_Z);
		return stack;
	}
	
	private List<IndexSymbol> getStack_X() {
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(ABSTRACT_STACK_SYMBOL);
		return stack;
	}
	
	private List<IndexSymbol> getStack_sX() {
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(STACK_SYMBOL_S);
		stack.add(ABSTRACT_STACK_SYMBOL);
		return stack;
	}
	
	private List<IndexSymbol> getStack_ssX() {
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(STACK_SYMBOL_S);
		stack.add(STACK_SYMBOL_S);
		stack.add(ABSTRACT_STACK_SYMBOL);
		return stack;
		
	}
	



	private HeapConfiguration getInputWithStackZ(List<IndexSymbol> stackForReferenceNt) {
		return getGraphWithReferenzNonterminalWithStack( getStack_Z(), stackForReferenceNt );
	}
	
	private HeapConfiguration getInputWithStack_sZ(List<IndexSymbol> stackForReferenceNt) {
		return getGraphWithReferenzNonterminalWithStack( getStack_sZ(), stackForReferenceNt );
	}
	

	private HeapConfiguration getInputWithStack_sX(List<IndexSymbol> stackForReferenceNt) {
		return getGraphWithReferenzNonterminalWithStack(getStack_sX(), stackForReferenceNt);
	}
	
	private HeapConfiguration getInputWithStack_ssX(List<IndexSymbol> stackForReferenceNt) {
		return getGraphWithReferenzNonterminalWithStack( getStack_ssX(), stackForReferenceNt );
	}
	

	

	


	private HeapConfiguration getExpected_sZ_BalancedRule(List<IndexSymbol> stackForReferenceNt) {
		List<IndexSymbol> stack_Z = getStack_Z();
		return getAppliedBalancedRuleWithStack(stack_Z, stackForReferenceNt );
	}
	
	private HeapConfiguration getExpected_sX_BalancedRule(List<IndexSymbol> stackForReferenceNt) {
		return getAppliedBalancedRuleWithStack(getStack_X(), stackForReferenceNt);
	}

	private HeapConfiguration getExpected_ssX_LeftRule(List<IndexSymbol> stackForReferenceNt) {
		return getAppliedLeftRuleWithStacks( getStack_sX(), getStack_X(), stackForReferenceNt );
	}
	
	private HeapConfiguration getExpected_ssX_RightRule(List<IndexSymbol> stackForReferenceNt) {
		return getAppliedRightRuleWithStacks( getStack_X(), getStack_sX(), stackForReferenceNt );
	}
	
	private HeapConfiguration getGraphWithReferenzNonterminalWithStack(List<IndexSymbol> stack,
			List<IndexSymbol> stackForReferenceNt) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		String label = BalancedTreeGrammar.NT_LABEL;
		int rank = BalancedTreeGrammar.NT_RANK;
		boolean[] isReductionTentacle = BalancedTreeGrammar.IS_REDUCTION_TENTACLE;
		IndexedNonterminal nt = new IndexedNonterminalImpl(label,rank,isReductionTentacle,stack);
		
		IndexedNonterminal referenceNt = new IndexedNonterminalImpl(label, stackForReferenceNt );
		Type type = BalancedTreeGrammar.TYPE;
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 4, nodes)
				.addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0) )
				.addNonterminalEdge(nt)
					.addTentacle(nodes.get(0) )
					.addTentacle( nodes.get(1) )
					.build()
				.addNonterminalEdge(referenceNt)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(3))
					.build()
				.build();			
	}
	
	private HeapConfiguration getAppliedBalancedLeafRule_WithReferenceStack(List<IndexSymbol> stackForReferenceNt) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		String label = BalancedTreeGrammar.NT_LABEL;
		IndexedNonterminal referenceNt = new IndexedNonterminalImpl(label , stackForReferenceNt  );
		
		Type type = BalancedTreeGrammar.TYPE;
		SelectorLabel leftLabel = BalancedTreeGrammar.SELECTOR_LEFT_0;
		SelectorLabel rightLabel = BalancedTreeGrammar.SELECTOR_RIGHT_0;
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 4, nodes)
				.addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0) )
				.addSelector(nodes.get(0), leftLabel, nodes.get(1) )
				.addSelector( nodes.get(0), rightLabel, nodes.get(1) )
				.addNonterminalEdge(referenceNt)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(3))
					.build()
				.build();
	}



	private HeapConfiguration getAppliedLeftLeafRuleWithReferenceStack(List<IndexSymbol> stackForReferenceNt) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = BalancedTreeGrammar.TYPE;
		SelectorLabel leftLabel = BalancedTreeGrammar.SELECTOR_LEFT_1;
		SelectorLabel rightLabel = BalancedTreeGrammar.SELECTOR_RIGHT_M1;
		SelectorLabel parentLabel = BalancedTreeGrammar.SELECTOR_PARENT;
		
		String label = BalancedTreeGrammar.NT_LABEL;
		int rank = BalancedTreeGrammar.NT_RANK;
		boolean[] isReductionTentacle = BalancedTreeGrammar.IS_REDUCTION_TENTACLE;
		List<IndexSymbol> stack_Z = getStack_Z();
		IndexedNonterminal nt = new IndexedNonterminalImpl(label,rank,isReductionTentacle,stack_Z);
		IndexedNonterminal referenceNt = new IndexedNonterminalImpl(label, stackForReferenceNt  );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 5, nodes)
				.addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0) )
				.addSelector(nodes.get(0), leftLabel, nodes.get(1) )
				.addSelector( nodes.get(1), parentLabel, nodes.get(0) )
				.addSelector( nodes.get(0), rightLabel, nodes.get(2) )
				.addNonterminalEdge(nt)
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(2))
					.build()
				.addNonterminalEdge(referenceNt)
					.addTentacle(nodes.get(3))
					.addTentacle(nodes.get(4))
					.build()
				.build();
	}
	
	private HeapConfiguration getAppliedRightLeafRuleWithReferenceStack(
			List<IndexSymbol> stackForReferenceNonterminal) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = BalancedTreeGrammar.TYPE;
		SelectorLabel leftLabel = BalancedTreeGrammar.SELECTOR_LEFT_M1;
		SelectorLabel rightLabel = BalancedTreeGrammar.SELECTOR_RIGHT_1;
		SelectorLabel parentLabel = BalancedTreeGrammar.SELECTOR_PARENT;
		
		String label = BalancedTreeGrammar.NT_LABEL;
		int rank = BalancedTreeGrammar.NT_RANK;
		boolean[] isReductionTentacle = BalancedTreeGrammar.IS_REDUCTION_TENTACLE;
		List<IndexSymbol> stack_Z = getStack_Z();
		IndexedNonterminal nt = new IndexedNonterminalImpl(label,rank,isReductionTentacle,stack_Z);
		IndexedNonterminal referenceNt = 
				new IndexedNonterminalImpl(label, stackForReferenceNonterminal );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 5, nodes)
				.addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0) )
				.addSelector(nodes.get(0), leftLabel, nodes.get(2) )
				.addSelector( nodes.get(0), rightLabel, nodes.get(1) )
				.addSelector( nodes.get(1), parentLabel, nodes.get(0) )
				.addNonterminalEdge(nt)
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(2))
					.build()
				.addNonterminalEdge(referenceNt)
					.addTentacle(nodes.get(3))
					.addTentacle(nodes.get(4))
					.build()
				.build();
	}
	
	private HeapConfiguration getAppliedBalancedRuleWithStack(List<IndexSymbol> stack,
						List<IndexSymbol> stackForReferenceNonterminal ) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = BalancedTreeGrammar.TYPE;
		SelectorLabel leftLabel = BalancedTreeGrammar.SELECTOR_LEFT_0;
		SelectorLabel rightLabel = BalancedTreeGrammar.SELECTOR_RIGHT_0;
		SelectorLabel parentLabel = BalancedTreeGrammar.SELECTOR_PARENT;
		
		String label = BalancedTreeGrammar.NT_LABEL;
		int rank = BalancedTreeGrammar.NT_RANK;
		boolean[] isReductionTentacle = BalancedTreeGrammar.IS_REDUCTION_TENTACLE;
		
		IndexedNonterminal nt = new IndexedNonterminalImpl(label,rank,isReductionTentacle,stack);
		IndexedNonterminal referenceNt = 
				new IndexedNonterminalImpl(label, stackForReferenceNonterminal );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 6, nodes)
				.addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0) )
				.addSelector(nodes.get(0), leftLabel, nodes.get(1) )
				.addSelector(nodes.get(1), parentLabel, nodes.get(0) )
				.addSelector( nodes.get(0), rightLabel, nodes.get(2) )
				.addSelector(nodes.get(2), parentLabel, nodes.get(0) )
				.addNonterminalEdge(nt)
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(3))
					.build()
				.addNonterminalEdge(nt)
					.addTentacle(nodes.get(2))
					.addTentacle( nodes.get(3))
					.build()
				.addNonterminalEdge(referenceNt)
					.addTentacle(nodes.get(4))
					.addTentacle(nodes.get(5))
					.build()
				.build();
	}
	
	private HeapConfiguration getAppliedLeftRuleWithStacks( List<IndexSymbol> leftStack,
															List<IndexSymbol> rightStack,
															List<IndexSymbol> stackForReferenceNonterminal) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = BalancedTreeGrammar.TYPE;
		SelectorLabel leftLabel = BalancedTreeGrammar.SELECTOR_LEFT_1;
		SelectorLabel rightLabel = BalancedTreeGrammar.SELECTOR_RIGHT_M1;
		SelectorLabel parentLabel = BalancedTreeGrammar.SELECTOR_PARENT;
		
		String label = BalancedTreeGrammar.NT_LABEL;
		int rank = BalancedTreeGrammar.NT_RANK;
		boolean[] isReductionTentacle = BalancedTreeGrammar.IS_REDUCTION_TENTACLE;
		
		IndexedNonterminal ntLeft = 
				new IndexedNonterminalImpl(label,rank,isReductionTentacle, leftStack);
		IndexedNonterminal ntRight =
				new IndexedNonterminalImpl(label, rightStack );
		IndexedNonterminal referenceNt = 
				new IndexedNonterminalImpl(label, stackForReferenceNonterminal );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 6, nodes)
				.addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0) )
				.addSelector(nodes.get(0), leftLabel, nodes.get(1) )
				.addSelector(nodes.get(1), parentLabel, nodes.get(0) )
				.addSelector( nodes.get(0), rightLabel, nodes.get(2) )
				.addSelector(nodes.get(2), parentLabel, nodes.get(0) )
				.addNonterminalEdge(ntLeft)
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(3))
					.build()
				.addNonterminalEdge(ntRight)
					.addTentacle(nodes.get(2))
					.addTentacle( nodes.get(3))
					.build()
				.addNonterminalEdge(referenceNt)
					.addTentacle(nodes.get(4))
					.addTentacle(nodes.get(5))
					.build()
				.build();
	}

	private HeapConfiguration getAppliedRightRuleWithStacks(List<IndexSymbol> leftStack,
															List<IndexSymbol> rightStack, List<IndexSymbol> stackForReferenceNonterminal) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = BalancedTreeGrammar.TYPE;
		SelectorLabel leftLabel = BalancedTreeGrammar.SELECTOR_LEFT_M1;
		SelectorLabel rightLabel = BalancedTreeGrammar.SELECTOR_RIGHT_1;
		SelectorLabel parentLabel = BalancedTreeGrammar.SELECTOR_PARENT;
		
		String label = BalancedTreeGrammar.NT_LABEL;
		int rank = BalancedTreeGrammar.NT_RANK;
		boolean[] isReductionTentacle = BalancedTreeGrammar.IS_REDUCTION_TENTACLE;
		
		IndexedNonterminal ntLeft = 
				new IndexedNonterminalImpl(label,rank,isReductionTentacle, leftStack);
		IndexedNonterminal ntRight =
				new IndexedNonterminalImpl(label, rightStack );
		IndexedNonterminal referenceNt = 
				new IndexedNonterminalImpl(label, stackForReferenceNonterminal );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 6, nodes)
				.addVariableEdge(VIOLATIONPOINT_VARIABLE, nodes.get(0) )
				.addSelector(nodes.get(0), leftLabel, nodes.get(1) )
				.addSelector(nodes.get(1), parentLabel, nodes.get(0) )
				.addSelector( nodes.get(0), rightLabel, nodes.get(2) )
				.addSelector(nodes.get(2), parentLabel, nodes.get(0) )
				.addNonterminalEdge(ntLeft)
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(3))
					.build()
				.addNonterminalEdge(ntRight)
					.addTentacle(nodes.get(2))
					.addTentacle( nodes.get(3))
					.build()
				.addNonterminalEdge(referenceNt)
					.addTentacle(nodes.get(4))
					.addTentacle(nodes.get(5))
					.build()
				.build();
	}

	
	

}
