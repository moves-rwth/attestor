package de.rwth.i2.attestor.grammar.materialization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.testUtil.TestGraphs;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;

public class ViolationPointResolverTest_Default {

	public static final GeneralNonterminal DEFAULT_NONTERMINAL =
			createDefaultNonterminal();
	public static final int TENTACLE_FOR_NEXT = 0;
	public static final int TENTACLE_WITHOUT_NEXT = 1;
	public static final String SELECTOR_NAME_NEXT = "next";
	public static final int TENTACLE_FOR_PREV = 1;
	public static final String SELECTOR_NAME_PREV = "prev";
	public static final HeapConfiguration RHS_CREATING_NEXT = 
			TestGraphs.getRuleGraph_CreatingNext();
	public static final HeapConfiguration RHS_CREATING_NEXT_PREV = 
			TestGraphs.getRuleGraph_CreatingNextAt0_PrevAt1();
	public static final HeapConfiguration RHS_CREATING_PREV = 
			TestGraphs.getRuleGraph_creatingPrevAt1();
	public static final HeapConfiguration RHS_CREATING_NO_SELECTOR = 
			TestGraphs.getRuleGraph_creatingNoSelector();


	@Test
	public void testgetRulesCreatingSelector_Successful() {
		Grammar testGrammar = Grammar.builder().addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT_PREV)
				.addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT)
				.addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NO_SELECTOR)
				.addRule(DEFAULT_NONTERMINAL, RHS_CREATING_PREV)
				.build();
		ViolationPointResolver grammarLogik = new ViolationPointResolver( testGrammar );


		Map<Nonterminal, Collection<HeapConfiguration>> selectedRules
			=  grammarLogik.getRulesCreatingSelectorFor(DEFAULT_NONTERMINAL, 
														TENTACLE_FOR_NEXT, 
														SELECTOR_NAME_NEXT );
		assertThat( selectedRules.keySet(), hasSize(1) );
		assertThat( selectedRules.get(DEFAULT_NONTERMINAL),
				containsInAnyOrder( RHS_CREATING_NEXT_PREV, RHS_CREATING_NEXT)
				);
		
		selectedRules = grammarLogik.getRulesCreatingSelectorFor(DEFAULT_NONTERMINAL, 
				TENTACLE_FOR_PREV, SELECTOR_NAME_PREV );
		assertThat( selectedRules.keySet(), hasSize(1) );
		assertThat( selectedRules.get(DEFAULT_NONTERMINAL),
				containsInAnyOrder( RHS_CREATING_NEXT_PREV, RHS_CREATING_PREV));
	}

	@Test
	public void testGetRulesCreatingSelector_WrongTentacle() {
		Grammar testGrammar = Grammar.builder().addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT_PREV)
				.addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT)
				.addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NO_SELECTOR)
				.addRule(DEFAULT_NONTERMINAL, RHS_CREATING_PREV)
				.build();
		ViolationPointResolver grammarLogik = new ViolationPointResolver( testGrammar );


		Map<Nonterminal, Collection<HeapConfiguration>> selectedRules 
				= grammarLogik.getRulesCreatingSelectorFor(DEFAULT_NONTERMINAL, 
						TENTACLE_WITHOUT_NEXT, 
						SELECTOR_NAME_NEXT ); 
		assertThat( selectedRules.keySet(),
				empty()
				);
	}

	@Test
	public void testGetRulesCreatingSelector_ImpossibleSelector(){
		Grammar testGrammar = Grammar.builder()
				.addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NEXT)
				.addRule(DEFAULT_NONTERMINAL, RHS_CREATING_NO_SELECTOR)
				.build();
		ViolationPointResolver grammarLogik = new ViolationPointResolver( testGrammar );


		Map<Nonterminal, Collection<HeapConfiguration>> selectedRules
			= grammarLogik.getRulesCreatingSelectorFor(DEFAULT_NONTERMINAL, 
					TENTACLE_WITHOUT_NEXT, 
					SELECTOR_NAME_PREV );
		assertThat( selectedRules.keySet(),
				empty()
				);
	}


	private static GeneralNonterminal createDefaultNonterminal(){
		return GeneralNonterminal.getNonterminal("GrammarLogikTest", 2, new boolean[]{false,false});
	}
}
