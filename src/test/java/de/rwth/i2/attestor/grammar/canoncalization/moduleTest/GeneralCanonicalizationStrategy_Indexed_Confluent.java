package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexCanonizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexVariable;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class GeneralCanonicalizationStrategy_Indexed_Confluent {

	private static final String NT_LABEL = "GeneralCanonicalizationStrategyIC";
	private static final int RANK = 2;
	private static final boolean[] isReductionTentacle = new boolean[RANK];
	private static final Type TYPE = Settings.getInstance().factory().getType("type");
	private static final SelectorLabel SEL = BasicSelectorLabel.getSelectorLabel("sel");
	
	private static final int sizeOfChain = 10;

	private IndexedCanonicalizationHelper matchingHandler;
	
	@Before
	public void init() {
		IndexCanonizationStrategy fakeIndexStrategy = new FakeIndexCanonicalizationStrategy();
		
		final int minDereferenceDepth = 1;
		final int aggressiveAbstractionThreshold = 10;
		final boolean aggressiveReturnAbstraction = false;
		EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(minDereferenceDepth ,
																				aggressiveAbstractionThreshold, 
																				aggressiveReturnAbstraction);
		
		IndexMaterializationStrategy materializer = new IndexMaterializationStrategy();
		DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();
		IndexMatcher indexMatcher = new IndexMatcher( indexGrammar);
		EmbeddingIndexChecker indexChecker = 
				new EmbeddingIndexChecker( indexMatcher, 
											materializer );
		
		matchingHandler = new IndexedCanonicalizationHelper( fakeIndexStrategy, checkerProvider, indexChecker);
		
	}

	@Test
	public void test() {
		
		List<IndexSymbol> lhsIndex1 = makeInstantiable(getIndexPrefix());
		Nonterminal lhs1 = getNonterminal( lhsIndex1  );
		HeapConfiguration rhs1 = getPattern1();
		HeapConfiguration rhs2 = getPattern2();
		Grammar grammar = Grammar.builder().addRule( lhs1, rhs1 )
										   .addRule(lhs1, rhs2)
										   .build();
		
		GeneralCanonicalizationStrategy canonizer 
				= new GeneralCanonicalizationStrategy( grammar, matchingHandler );
		
		ProgramState inputState = new DefaultProgramState( getInputGraph() );
		Statement stmt = new Skip( 0 );
		
		ProgramState res = canonizer.canonicalize(stmt, inputState);
		
		assertEquals( expectedSimpleAbstraction().getHeap(), res.getHeap() );
	}



	private List<IndexSymbol> getEmptyIndex() {
		List<IndexSymbol> index = new ArrayList<>();
		return index;
	}
	
	private List<IndexSymbol> getIndexPrefix() {
		List<IndexSymbol> index = getEmptyIndex();
		index.add( DefaultIndexMaterialization.SYMBOL_s );
		return index;
	}

	private List<IndexSymbol> makeConcrete( List<IndexSymbol> index ){
		List<IndexSymbol> indexCopy = new ArrayList<>( index );
		indexCopy.add( DefaultIndexMaterialization.SYMBOL_Z );
		return indexCopy;
	}
	
	private List<IndexSymbol> makeInstantiable( List<IndexSymbol> index ){
		List<IndexSymbol> indexCopy = new ArrayList<>( index );
		indexCopy.add( IndexVariable.getIndexVariable() );
		return indexCopy;
	}


	private Nonterminal getNonterminal( List<IndexSymbol> index ) {
		return new IndexedNonterminalImpl(NT_LABEL, RANK, isReductionTentacle, index);
	}


	private HeapConfiguration getPattern1() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(TYPE, 3, nodes)
				.addNonterminalEdge( getNonterminal( makeInstantiable(getEmptyIndex()) ))
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
				.addNonterminalEdge( getNonterminal( makeInstantiable(getEmptyIndex()) ))
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
		builder.addNonterminalEdge( getNonterminal( makeConcrete(getEmptyIndex())))
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build();
		
		return	builder.build();
	}
	
	private ProgramState expectedSimpleAbstraction() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		List<IndexSymbol> expectedIndex = getExpectedIndex();
		
		TIntArrayList nodes = new TIntArrayList();
		hc =  hc.builder().addNodes(TYPE, 2, nodes)
				.addNonterminalEdge( getNonterminal(expectedIndex ))
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build()
				.build();
		
		return new IndexedState( hc );
	}

	private List<IndexSymbol> getExpectedIndex() {
		List<IndexSymbol> index = getEmptyIndex();
		for( int i = 0; i < sizeOfChain - 1; i++ ) {
			index.add( DefaultIndexMaterialization.SYMBOL_s );
		}
		return makeConcrete( index );
	}


}
