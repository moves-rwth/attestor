package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingStackChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedMatchingHandler;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.StackMaterializer;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.DefaultStackMaterialization;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackVariable;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

public class GeneralCanonicalizationStrategy_Indexed_Confluent {

	private static final String NT_LABEL = "GeneralCanonicalizationStrategyIC";
	private static final int RANK = 2;
	private static final boolean[] isReductionTentacle = new boolean[RANK];
	private static final Type TYPE = TypeFactory.getInstance().getType("type");
	private static final SelectorLabel SEL = GeneralSelectorLabel.getSelectorLabel("sel");
	
	private static final int sizeOfChain = 10;

	private IndexedMatchingHandler matchingHandler;
	
	@Before
	public void init() {
		EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(10, false);
		
		StackMaterializer materializer = new StackMaterializer();
		DefaultStackMaterialization stackGrammar = new DefaultStackMaterialization();
		StackMatcher stackMatcher = new StackMatcher( stackGrammar);
		EmbeddingStackChecker stackChecker = 
				new EmbeddingStackChecker( stackMatcher, 
											materializer );
		
		matchingHandler = new IndexedMatchingHandler(checkerProvider, stackChecker);
		
	}

	@Test
	public void test() {
//		List<StackSymbol> lhsStack0 = makeConcrete( getEmptyStack() );
//		Nonterminal lhs0 = getNonterminal( lhsStack0 );
//		HeapConfiguration rhs0 = getPattern0();
		
		List<StackSymbol> lhsStack1 = makeInstantiable(getStackPrefix());
		Nonterminal lhs1 = getNonterminal( lhsStack1  );
		HeapConfiguration rhs1 = getPattern1();
		HeapConfiguration rhs2 = getPattern2();
		Grammar grammar = Grammar.builder().addRule( lhs1, rhs1 )
										   .addRule(lhs1, rhs2)
										   .build();
		
		GeneralCanonicalizationStrategy canonizer 
				= new GeneralCanonicalizationStrategy( grammar, matchingHandler );
		
		ProgramState inputState = new DefaultState( getInputGraph() );
		Statement stmt = new Skip( 0 );
		
		Set<ProgramState> res = canonizer.canonicalize(stmt, inputState);
		
		assertEquals( 1, res.size() );
		assertEquals( expectedSimpleAbstraction().getHeap(), res.iterator().next().getHeap() );
	}



	private List<StackSymbol> getEmptyStack() {
		List<StackSymbol> stack = new ArrayList<>();
		return stack;
	}
	
	private List<StackSymbol> getStackPrefix() {
		List<StackSymbol> stack = getEmptyStack();
		stack.add( DefaultStackMaterialization.SYMBOL_s );
		return stack;
	}

	private List<StackSymbol> makeConcrete( List<StackSymbol> stack ){
		List<StackSymbol> stackCopy = new ArrayList<>( stack );
		stackCopy.add( DefaultStackMaterialization.SYMBOL_Z );
		return stackCopy;
	}
	
	private List<StackSymbol> makeInstantiable( List<StackSymbol> stack ){
		List<StackSymbol> stackCopy = new ArrayList<>( stack );
		stackCopy.add( StackVariable.getGlobalInstance() );
		return stackCopy;
	}


	private Nonterminal getNonterminal( List<StackSymbol> stack ) {
		return new IndexedNonterminalImpl(NT_LABEL, RANK, isReductionTentacle, stack);
	}

	private HeapConfiguration getPattern0() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(TYPE, 2, nodes)
				.addSelector(nodes.get(0), SEL , nodes.get(1) )
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.build();
	}


	private HeapConfiguration getPattern1() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(TYPE, 3, nodes)
				.addNonterminalEdge( getNonterminal( makeInstantiable(getEmptyStack()) ))
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build()
				.addSelector(nodes.get(1), SEL , nodes.get(2) )
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(2))
				.build();
	}
	


	private HeapConfiguration getPattern2() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(TYPE, 3, nodes)
				.addSelector(nodes.get(2), SEL , nodes.get(1) )
				.addNonterminalEdge( getNonterminal( makeInstantiable(getEmptyStack()) ))
					.addTentacle(nodes.get(1))
					.addTentacle(nodes.get(2))
					.build()
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(2))
				.build();
	}
	


	private HeapConfiguration getInputGraph() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder().addNodes(TYPE, sizeOfChain + 1, nodes);
		for( int i = 1; i < sizeOfChain ; i++ ) {
				builder.addSelector(nodes.get(i), SEL , nodes.get(i+1) );
		}
		builder.addNonterminalEdge( getNonterminal( makeConcrete(getEmptyStack())))
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build();
		
		return	builder.build();
	}
	
	private ProgramState expectedSimpleAbstraction() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		List<StackSymbol> expectedStack = getExpectedStack();
		
		TIntArrayList nodes = new TIntArrayList();
		hc =  hc.builder().addNodes(TYPE, 2, nodes)
				.addNonterminalEdge( getNonterminal(expectedStack ))
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build()
				.build();
		
		return new IndexedState( hc );
	}

	private List<StackSymbol> getExpectedStack() {
		List<StackSymbol> stack = getEmptyStack();
		for( int i = 0; i < sizeOfChain - 1; i++ ) {
			stack.add( DefaultStackMaterialization.SYMBOL_s );
		}
		return makeConcrete( stack );
	}


}
