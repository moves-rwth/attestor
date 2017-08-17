package de.rwth.i2.attestor.grammar.canoncalization.moduleTest;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.*;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultMatchingHandler;
import de.rwth.i2.attestor.graph.*;
import de.rwth.i2.attestor.graph.heap.*;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class GeneralCanonicalizationTest_Default_ConfluentTest {

	private static int RANK = 2;
	private static final Type TYPE = Settings.getInstance().factory().getType("type");
	private static final SelectorLabel SEL = GeneralSelectorLabel.getSelectorLabel("sel");
	MatchingHandler matchingHandler;
	
	@Before
	public void setUp() throws Exception {
		final int minDereferenceDepth = 1;
		final int aggressiveAbstractionThreshold = 10;
		final boolean aggressiveReturnAbstraction = false;
		EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(minDereferenceDepth ,
																				aggressiveAbstractionThreshold, 
																				aggressiveReturnAbstraction);
		matchingHandler = new DefaultMatchingHandler( checkerProvider );
	}
	
	@Test
	public void test() {
		Nonterminal lhs = getNonterminal();
		HeapConfiguration rhs1 = getPattern1();
		HeapConfiguration rhs2 = getPattern2();
		Grammar grammar = Grammar.builder().addRule( lhs, rhs1 )
										   .addRule(lhs, rhs2)
										   .build();
		
		GeneralCanonicalizationStrategy canonizer 
				= new GeneralCanonicalizationStrategy( grammar, matchingHandler );
		
		ProgramState inputState = new DefaultProgramState( getInputGraph() );
		Statement stmt = new Skip( 0 );
		
		Set<ProgramState> res = canonizer.canonicalize(stmt, inputState);
		
		assertEquals( 1, res.size() );
		assertThat( res, contains( expectedFullAbstraction(lhs) ) );
	}


	private Nonterminal getNonterminal() {
		boolean[] isReductionTentacle = new boolean[RANK];
		return GeneralNonterminal.getNonterminal("some label", RANK, isReductionTentacle );
	}
	
	private HeapConfiguration getPattern1() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder().addNodes(TYPE, RANK, nodes)
				.addSelector(nodes.get(0), SEL , nodes.get(1) );
		for( int i = 0; i < RANK; i++ ){
			builder.setExternal( nodes.get(i) );
		}
		return builder.build();
	}
	
	private HeapConfiguration getPattern2() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder().addNodes(TYPE, RANK + 1, nodes)
				.addNonterminalEdge(getNonterminal())
					.addTentacle( nodes.get(0))
					.addTentacle( nodes.get(1) )
					.build()
					.addNonterminalEdge(getNonterminal())
					.addTentacle( nodes.get(1))
					.addTentacle( nodes.get(2) )
					.build()
				.setExternal(nodes.get(0))
				.setExternal( nodes.get(2));
		return builder.build();
	}
	
	private HeapConfiguration getInputGraph() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		int sizeOfChain = 10;
		
		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder().addNodes(TYPE, sizeOfChain + 1, nodes);
		for( int i = 0; i < sizeOfChain; i++ ){
			builder.addSelector( nodes.get(i), SEL, nodes.get(i+1) );
		}
		return builder.build();
	}
	
	private ProgramState expectedFullAbstraction(Nonterminal lhs) {
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
