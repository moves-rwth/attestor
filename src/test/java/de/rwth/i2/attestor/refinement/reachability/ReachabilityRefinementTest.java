package de.rwth.i2.attestor.refinement.reachability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.Index;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.RefinedNonterminal;
import de.rwth.i2.attestor.refinement.grammarRefinement.GrammarRefinement;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;

public class ReachabilityRefinementTest {

	Scene scene = new MockupSceneObject().scene();

	/**
	 * test using a simple SLL grammar
	 * SLL -> .-next->., .-next->.-SLL-.
	 */
	@Test
	public void testSimpleSLL() {
		SelectorLabel sel = scene.getSelectorLabel("next");
		Nonterminal nt = getNonterminal("SLL");

		Grammar grammar = simpleInputGrammar( nt, sel );

		//we do not specify selectors since this test is only interested in reachability and not in APs
		HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton(scene, new HashSet<>() );
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
		SelectorLabel sel = scene.getSelectorLabel("next");
		Nonterminal nt1 = getNonterminal("SLL1");
		Nonterminal nt2 = getNonterminal("SLL2");

		Grammar grammar = inputGrammarWithIndirection(nt1, nt2, sel);

		//we do not specify selectors since this test is only interested in reachability and not in APs
		HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton(scene, new HashSet<>() );
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
		SelectorLabel sel1 = scene.getSelectorLabel("next1");
		SelectorLabel sel2 = scene.getSelectorLabel("next2");
		Nonterminal nt1 = getNonterminal("SLL1");
		Nonterminal nt2 = getNonterminal("SLL2");

		Grammar grammar = inputGrammarWithMultipleSelectors(nt1, nt2, sel1, sel2);

		//we do not specify selectors since this test is only interested in reachability and not in APs
		HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton(scene, new HashSet<>() );
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
	 * test using a SLL grammar with indices
	 * SLL[sX] -> .-next->.-SLL[X]-.
	 * SLL[Z] -> .-next->.
	 */
	@Test
	public void testSLLWithIndices() {
		BasicNonterminal nonterminal = getNonterminal("SLL");

		Grammar grammar = indexedInputGrammar(nonterminal);
		assertEquals( 2, grammar.getAllLeftHandSides().size() );

		//we do not specify selectors since this test is only interested in reachability and not in APs
		HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton(scene, new HashSet<>() );
		GrammarRefinement refinement = new GrammarRefinement( grammar, reachabilityAutomaton  );

		Grammar refinedGrammar = refinement.getRefinedGrammar();

		Collection<Nonterminal> ntsInRefinedGrammar = refinedGrammar.getAllLeftHandSides();
		assertEquals( 1,  ntsInRefinedGrammar.size() );//richtig?
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
	
	//indexed SLL grammar
	private Grammar indexedInputGrammar( BasicNonterminal nt ) {
		SelectorLabel sel = scene.getSelectorLabel("next");
		IndexVariable indexVariable = new IndexVariable();
		
		List<IndexSymbol> indexOnRhs = SingleElementUtil.createList( indexVariable );
		Nonterminal ntOnRhs = new IndexedNonterminalImpl(nt, indexOnRhs);
		
		HeapConfiguration recursiveRule = singlePointerToNonterminalEdge(ntOnRhs, sel );
		HeapConfiguration baseRule = singlePointerLeftToRight(sel);
		
		List<IndexSymbol> index1 = new ArrayList<>();
		index1.add( ConcreteIndexSymbol.getIndexSymbol("s", false));
		index1.add(indexVariable);
		Nonterminal lhs1 = new IndexedNonterminalImpl(nt, index1);
		
		
		List<IndexSymbol> index2 = SingleElementUtil.createList( ConcreteIndexSymbol.getIndexSymbol("Z", true) );
		Nonterminal lhs2 = new IndexedNonterminalImpl(nt, index2);
		
		return Grammar.builder().addRule(lhs1, recursiveRule)
								.addRule(lhs2, baseRule)
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

	private BasicNonterminal getNonterminal(String label) {
		return (BasicNonterminal) scene.createNonterminal(label, 2, new boolean[] {false,false} );
	}

}
