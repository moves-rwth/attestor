package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class GeneralCanonicalizationStrategy_Indexed_Simple {

	private static final String NT_LABEL = "GeneralCanonicalizationStrategyIS";
	private static final int RANK = 2;
	private static final boolean[] isReductionTentacle = new boolean[RANK];
	private static final Type TYPE = Settings.getInstance().factory().getType("type");
	private static final SelectorLabel SEL = GeneralSelectorLabel.getSelectorLabel("sel");

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
		
		matchingHandler = new IndexedCanonicalizationHelper(fakeIndexStrategy,checkerProvider, indexChecker);
		
	}

	@Test
	public void test() {
		
		List<IndexSymbol> lhsIndex = makeInstantiable(getIndexPrefix());
		Nonterminal lhs = getNonterminal( lhsIndex  );
		HeapConfiguration rhs = getPattern();
		Grammar grammar = Grammar.builder().addRule( lhs, rhs ).build();
		
		GeneralCanonicalizationStrategy canonizer 
				= new GeneralCanonicalizationStrategy( grammar, matchingHandler );
		
		ProgramState inputState = new DefaultProgramState( getSimpleGraph() );
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



	private HeapConfiguration getPattern() {
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
	

	private HeapConfiguration getSimpleGraph() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(TYPE, 3, nodes)
				.addNonterminalEdge( getNonterminal( makeConcrete(getEmptyIndex()) ))
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build()
				.addSelector(nodes.get(1), SEL , nodes.get(2) )
				.build();
	}
	
	private ProgramState expectedSimpleAbstraction() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		hc =  hc.builder().addNodes(TYPE, 2, nodes)
				.addNonterminalEdge( getNonterminal( makeConcrete(getIndexPrefix()) ))
					.addTentacle(nodes.get(0))
					.addTentacle(nodes.get(1))
					.build()
				.build();
		
		return new IndexedState( hc );
	}


}
