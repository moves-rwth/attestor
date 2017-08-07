package de.rwth.i2.attestor.grammar.canoncalization;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultState;

public class GeneralCanonicalizationStrategyTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSimpleDefault() {
		
		HeapConfiguration pattern = getSimpleInput();
		Nonterminal lhs = getDefaultNonterminal();
		
		Grammar grammar = Grammar.builder().addRule(lhs, pattern).build();
		
		GeneralCanonicalizationStrategy canonicalizer = 
				new GeneralCanonicalizationStrategy( grammar, new DefaultEmbeddingFinder() );
		
		HeapConfiguration toAbstract = getSimpleInput();
		ProgramState input = new DefaultState(toAbstract);
		
		Set<ProgramState> res = canonicalizer.canonicalize(getSomeSemantics(), input);
		
		assertThat( res, contains( getSimpleAbstracted( lhs ) ) );
	}

}
