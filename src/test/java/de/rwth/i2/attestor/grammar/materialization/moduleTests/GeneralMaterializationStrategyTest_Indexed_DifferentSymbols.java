package de.rwth.i2.attestor.grammar.materialization.moduleTests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.*;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.AbstractStackSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.ConcreteStackSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.DefaultStackMaterialization;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackVariable;
import de.rwth.i2.attestor.stateSpaceGeneration.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeneralMaterializationStrategyTest_Indexed_DifferentSymbols {

	private static final boolean[] REDUCTION_TENTACLEs = new boolean[] {false,false};
	private static final int RANK = 2;
	private static final String LABEL = "TestDifferentSymbols";
	private static final String VIOLATION_POINT_VARIABLE = "x";
	MaterializationStrategy materializer;
	AbstractStackSymbol oneAbstractSymbol;
	AbstractStackSymbol otherAbstractSymbol;
	private static final String VIOLATION_POINT_SELECTOR = "next";

	@BeforeClass
	public static void init() {
		UnitTestGlobalSettings.reset();
	}

	@Before
	public void setUp() {
		oneAbstractSymbol = DefaultStackMaterialization.SYMBOL_X;
		otherAbstractSymbol = DefaultStackMaterialization.SYMBOL_Y;
		Grammar grammar = buildSimpleGrammarWithTwoStackGrammars();
		ViolationPointResolver vioResolver = new ViolationPointResolver(grammar);
		StackMatcher stackMatcher = new StackMatcher( new DefaultStackMaterialization() );
		IndexedMaterializationRuleManager grammarManager = 
				new IndexedMaterializationRuleManager(vioResolver, stackMatcher);
		
		IndexedGrammarResponseApplier ruleApplier = 
				new IndexedGrammarResponseApplier(new StackMaterializer(), new GraphMaterializer() );
		
		materializer = new GeneralMaterializationStrategy( grammarManager, ruleApplier );
	}



	@Test
	public void test() {
		ViolationPoints inputViolationPoint = new ViolationPoints();
		inputViolationPoint.add(VIOLATION_POINT_VARIABLE, VIOLATION_POINT_SELECTOR );
		

		HeapConfiguration inputGraph = getInput();
		ProgramState inputState = new IndexedState(inputGraph).prepareHeap();
		
		HeapConfiguration expectedGraph = getExpected();
		ProgramState expectedState = new IndexedState(expectedGraph).prepareHeap();
		
		Collection<ProgramState> result = materializer.materialize(inputState, inputViolationPoint);
		
		assertThat( result, contains( expectedState) );
		
	}


	private HeapConfiguration getInput() {
		Type someType = TypeFactory.getInstance().getType("type");
		
		List<StackSymbol> stackWithOneStackSymbol = SingleElementUtil.createList( oneAbstractSymbol );
		Nonterminal toReplace = getNonterminalWithStack( stackWithOneStackSymbol);
		Nonterminal controlWithSameStack = getNonterminalWithStack( stackWithOneStackSymbol);
		Nonterminal controlWithOtherStack = getNonterminalWithStack( SingleElementUtil.createList( otherAbstractSymbol) );
		
		TIntArrayList nodes = new TIntArrayList();
		return new InternalHeapConfiguration().builder()
				.addNodes(someType, 4, nodes)
				.addVariableEdge(VIOLATION_POINT_VARIABLE, nodes.get(0) )
				.addNonterminalEdge(toReplace)
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build()
				.addNonterminalEdge(controlWithSameStack)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(3))
					.build()
				.addNonterminalEdge(controlWithOtherStack)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(3))
					.build()
				.build();
	}
	
	private HeapConfiguration getExpected() {
		Type someType = TypeFactory.getInstance().getType("type");
		GeneralSelectorLabel selectorLabel = GeneralSelectorLabel.getSelectorLabel(VIOLATION_POINT_SELECTOR);
		
		List<StackSymbol> stackWithOneStackSymbol = SingleElementUtil.createList( oneAbstractSymbol );
		List<StackSymbol> materializedStack = stackForLhs();
		materializedStack.add(oneAbstractSymbol);
		Nonterminal controlWithSameStack = getNonterminalWithStack( materializedStack );
		Nonterminal controlWithOtherStack = getNonterminalWithStack( SingleElementUtil.createList( otherAbstractSymbol) );
		
		TIntArrayList nodes = new TIntArrayList();
		return new InternalHeapConfiguration().builder()
				.addNodes(someType, 4, nodes)
				.addVariableEdge(VIOLATION_POINT_VARIABLE, nodes.get(0) )
				.addSelector(nodes.get(0), selectorLabel, nodes.get(1))
				.addNonterminalEdge(controlWithSameStack)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(3))
					.build()
				.addNonterminalEdge(controlWithOtherStack)
					.addTentacle(nodes.get(2))
					.addTentacle(nodes.get(3))
					.build()
				.build();
	}



	private Grammar buildSimpleGrammarWithTwoStackGrammars() {
		return Grammar.builder()
				.addRule( getNonterminalWithStackVariable(), someRhs() )
				.build();
	}


	private List<StackSymbol> stackForLhs(){
		List<StackSymbol> stack = new ArrayList<>();
		stack.add( ConcreteStackSymbol.getStackSymbol("s", true) );
		return stack;
	}
	
	private Nonterminal getNonterminalWithStackVariable() {
		List<StackSymbol> stack = stackForLhs();
		stack.add( StackVariable.getGlobalInstance() );
		return getNonterminalWithStack( stack );
	}
	
	private Nonterminal getNonterminalWithStack( List<StackSymbol> stack ) {
		return new IndexedNonterminalImpl(LABEL, RANK, REDUCTION_TENTACLEs, stack);
	}
	
	private HeapConfiguration someRhs() {
		Type someType = TypeFactory.getInstance().getType("type");
		GeneralSelectorLabel selectorLabel = GeneralSelectorLabel.getSelectorLabel(VIOLATION_POINT_SELECTOR);
		
		TIntArrayList nodes = new TIntArrayList();
		return new InternalHeapConfiguration().builder()
				.addNodes(someType, 2, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.addSelector(nodes.get(0), selectorLabel, nodes.get(1))
				.build();		
	}
	
}
