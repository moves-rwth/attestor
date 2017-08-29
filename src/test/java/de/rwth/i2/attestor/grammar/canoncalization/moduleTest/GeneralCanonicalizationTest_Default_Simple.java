package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import static org.junit.Assert.assertEquals;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.NonterminalEdgeBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class GeneralCanonicalizationTest_Default_Simple {
	
	private static final int RANK = 3;
	private static final Type TYPE = Settings.getInstance().factory().getType("type");
	private static final SelectorLabel SEL = BasicSelectorLabel.getSelectorLabel("sel");
	CanonicalizationHelper canonicalizationHelper;
	
	@Before
	public void setUp() throws Exception {final int minDereferenceDepth = 1;
	final int aggressiveAbstractionThreshold = 10;
	final boolean aggressiveReturnAbstraction = false;
	EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(minDereferenceDepth ,
																			aggressiveAbstractionThreshold, 
																			aggressiveReturnAbstraction);
		canonicalizationHelper = new DefaultCanonicalizationHelper( checkerProvider );
	}

	@Test
	public void testSimple() {
		Nonterminal lhs = getNonterminal();
		HeapConfiguration rhs = getPattern();
		Grammar grammar = Grammar.builder().addRule( lhs, rhs ).build();
		
		GeneralCanonicalizationStrategy canonizer 
				= new GeneralCanonicalizationStrategy( grammar, canonicalizationHelper );
		
		ProgramState inputState = new DefaultProgramState( getSimpleGraph() );
		Statement stmt = new Skip( 0 );
		
		ProgramState res = canonizer.canonicalize(stmt, inputState);

		assertEquals( expectedSimpleAbstraction(lhs), res);
		
	}


	private Nonterminal getNonterminal() {
		boolean[] isReductionTentacle = new boolean[RANK];
		return BasicNonterminal.getNonterminal("GeneralCanonicalizationDS", RANK, isReductionTentacle );
	}

	private HeapConfiguration getPattern() {
		HeapConfiguration hc = new InternalHeapConfiguration();
				
		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder().addNodes(TYPE, RANK, nodes)
				.addSelector(nodes.get(0), SEL , nodes.get(1) );
		for( int i = 0; i < RANK; i++ ){
			builder.setExternal( nodes.get(i) );
		}
		return builder.build();
	}
	
	private HeapConfiguration getSimpleGraph() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(TYPE, RANK, nodes)
				.addSelector(nodes.get(0), SEL , nodes.get(1) )
				.build();
	}
	
	private ProgramState expectedSimpleAbstraction(Nonterminal lhs) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		NonterminalEdgeBuilder builder =  hc.builder().addNodes(TYPE, RANK, nodes)
				.addNonterminalEdge(lhs);
		for( int i = 0; i < RANK; i++ ){
			builder.addTentacle( nodes.get(i) );
		}
		hc =  builder.build().build();
		return new DefaultProgramState( hc );
	}
}
