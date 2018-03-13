package de.rwth.i2.attestor.refinement.reachability;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.RefinedNonterminal;
import de.rwth.i2.attestor.refinement.grammarRefinement.GrammarRefinement;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;

public class TestReachabilityRefinement {

	Scene scene = new MockupSceneObject().scene();

	/**
	 * test using a simple SLL grammar
	 * SLL -> .-next->., .-next->.-SLL-.
	 */
	@Test
	public void testSimpleSLL() {
		String selectorLabel = "next";
		SelectorLabel sel = scene.getSelectorLabel(selectorLabel);
		Set<String> selectorLabels = SingleElementUtil.createSet( selectorLabel );
		Nonterminal nt = getNonterminal("SLL");

		Grammar grammar = simpleInputGrammar( nt, sel );

		HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton(scene, selectorLabels );
		GrammarRefinement refinement = new GrammarRefinement( grammar, reachabilityAutomaton  );

		Grammar refinedGrammar = refinement.getRefinedGrammar();

		Collection<Nonterminal> ntsInRefinedGrammar = refinedGrammar.getAllLeftHandSides();
		assertEquals( 1,  ntsInRefinedGrammar.size() );
		RefinedNonterminal refinedNt = (RefinedNonterminal) ntsInRefinedGrammar.iterator().next();
		ReachabilityAutomatonState state = (ReachabilityAutomatonState) refinedNt.getState();
		assertThat( state.reachableSetFrom(0), contains(1) );
		assertThat( state.reachableSetFrom(1), empty() );	
	}

	/**
	 * test using a SLL grammar, where one nonterminal depends on the other
	 * SLL1 -> .-next->.-SLL2-.
	 * SLL2 -> .-next->., .-next->.-SLL1-.
	 */
	@Test
	public void testSLLWithIndirection() {
		String selectorLabel = "next";
		SelectorLabel sel = scene.getSelectorLabel(selectorLabel);
		Set<String> selectorLabels = SingleElementUtil.createSet( selectorLabel );
		Nonterminal nt1 = getNonterminal("SLL1");
		Nonterminal nt2 = getNonterminal("SLL2");

		Grammar grammar = inputGrammarWithIndirection(nt1, nt2, sel);

		HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton(scene, selectorLabels );
		GrammarRefinement refinement = new GrammarRefinement( grammar, reachabilityAutomaton  );

		Grammar refinedGrammar = refinement.getRefinedGrammar();

		Collection<Nonterminal> ntsInRefinedGrammar = refinedGrammar.getAllLeftHandSides();
		assertEquals( 2,  ntsInRefinedGrammar.size() );
		for( Nonterminal nt : ntsInRefinedGrammar ) {
			RefinedNonterminal refinedNt1 = (RefinedNonterminal) nt;
			ReachabilityAutomatonState state = (ReachabilityAutomatonState) refinedNt1.getState();
			assertThat( state.reachableSetFrom(0), contains(1) );
			assertThat( state.reachableSetFrom(1), empty() );	
		}
	}
	
	/**
	 * test using a SLL grammar, where one nonterminal depends on the other
	 * and they use different selectors
	 * SLL1 -> .-next1->.-SLL2-.
	 * SLL2 -> .-next2->., .-next2->.-SLL1-.
	 */
	@Test
	public void testSLLWithDifferentSelectors() {
		String selectorLabel1 = "next1";
		String selectorLabel2 = "next2";
		SelectorLabel sel1 = scene.getSelectorLabel(selectorLabel1);
		SelectorLabel sel2 = scene.getSelectorLabel(selectorLabel2);
		Set<String> selectorLabels = new HashSet<>();
		selectorLabels.add(selectorLabel1);
		selectorLabels.add(selectorLabel2);
		Nonterminal nt1 = getNonterminal("SLL1");
		Nonterminal nt2 = getNonterminal("SLL2");

		Grammar grammar = inputGrammarWithMultipleSelectors(nt1, nt2, sel1, sel2);

		HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton(scene, selectorLabels );
		GrammarRefinement refinement = new GrammarRefinement( grammar, reachabilityAutomaton  );

		Grammar refinedGrammar = refinement.getRefinedGrammar();

		Collection<Nonterminal> ntsInRefinedGrammar = refinedGrammar.getAllLeftHandSides();
		assertEquals( 2,  ntsInRefinedGrammar.size() );
		for( Nonterminal nt : ntsInRefinedGrammar ) {
			RefinedNonterminal refinedNt1 = (RefinedNonterminal) nt;
			ReachabilityAutomatonState state = (ReachabilityAutomatonState) refinedNt1.getState();
			assertThat( state.reachableSetFrom(0), contains(1) );
			assertThat( state.reachableSetFrom(1), empty() );	
		}
	}

	//simple SLL-like grammar
	private Grammar simpleInputGrammar(Nonterminal nt, SelectorLabel sel) {

		HeapConfiguration baseRule = singlePointerLeftToRight( sel );
		HeapConfiguration recursiveRule = singlePointerToNonterminalEdge( nt, sel );

		return Grammar.builder().addRule(nt, baseRule)
				.addRule(nt, recursiveRule)
				.build();

	}

	//sll like grammar, but with indirection
	private Grammar inputGrammarWithIndirection( Nonterminal nt1, Nonterminal nt2, SelectorLabel sel ) {
		HeapConfiguration baseRule = singlePointerLeftToRight(sel);
		HeapConfiguration recursiveRule1 = singlePointerToNonterminalEdge(nt1, sel);

		HeapConfiguration recursiveRule2 = singlePointerToNonterminalEdge(nt2, sel);

		return Grammar.builder().addRule(nt1, recursiveRule2)
				.addRule(nt2, baseRule)
				.addRule(nt2, recursiveRule1)
				.build();
	}
	
	private Grammar inputGrammarWithMultipleSelectors( Nonterminal nt1, Nonterminal nt2, 
													   SelectorLabel sel1, SelectorLabel sel2 ) {
		
		HeapConfiguration recursiveRule1 = singlePointerToNonterminalEdge(nt2, sel1);
		
		HeapConfiguration recursiveRule2 = singlePointerToNonterminalEdge(nt1, sel2);
		HeapConfiguration baseRule = singlePointerLeftToRight(sel2);
		
		return Grammar.builder().addRule(nt1, recursiveRule1)
								.addRule(nt2, recursiveRule2)
								.addRule(nt2, baseRule)
								.build();
		
	}

	private HeapConfiguration singlePointerToNonterminalEdge(Nonterminal nt, SelectorLabel sel) {
		HeapConfigurationBuilder builder = scene.createHeapConfiguration().builder();

		TIntArrayList nodes = new TIntArrayList();
		Type type = scene.getType("node");
		return builder.addNodes(type, 3, nodes)
				.addSelector(nodes.get(0), sel, nodes.get(1) )
				.addNonterminalEdge(nt)
				.addTentacle( nodes.get(1) )
				.addTentacle( nodes.get(2) )
				.build()
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(2) )
				.build();
	}

	private HeapConfiguration singlePointerLeftToRight(SelectorLabel sel) {
		HeapConfigurationBuilder builder = scene.createHeapConfiguration().builder();

		TIntArrayList nodes = new TIntArrayList();
		Type type = scene.getType("node");
		return builder.addNodes(type, 2, nodes)
				.addSelector(nodes.get(0), sel, nodes.get(1) )
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(1) )
				.build();

	}

	private Nonterminal getNonterminal(String label) {
		return scene.createNonterminal(label, 2, new boolean[] {false,false} );
	}

}
