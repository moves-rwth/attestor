package de.rwth.i2.attestor.refinement.reachability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
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
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;

public class ReachabilityComputerTest {
	static Scene scene;
	static ReachabilityComputer testObject;
	
	@Before
	public void init() {
		scene  = new MockupSceneObject().scene();
		testObject = new ReachabilityComputer(scene);
	}
	/**
	 * test using a simple SLL grammar
	 * SLL -> .-next->., .-next->.-SLL-.
	 */
	@Test
	public void testSimpleSLL() {
		SelectorLabel sel = scene.getSelectorLabel("next");
		Nonterminal nt = getNonterminal("SLL");

		Grammar grammar = simpleInputGrammar( nt, sel );

		testObject.precomputeReachability(grammar);
		
		assertThat( nt.reachableTentaclesFrom(0), contains(1) );
		assertThat( nt.reachableTentaclesFrom(1), empty() );
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

		testObject.precomputeReachability(grammar);
		
		assertThat( nt1.reachableTentaclesFrom(0), contains(1) );
		assertThat( nt1.reachableTentaclesFrom(1), empty() );
		
		assertThat( nt2.reachableTentaclesFrom(0), contains(1) );
		assertThat( nt2.reachableTentaclesFrom(1), empty() );
	}
	
	/**
	 * Test using a grammar where one rule implies reachability from 1 to 2
	 * and the other from 2 to 1.
	 */
	@Test
	public void testWithRulesNotConsistent() {
		Nonterminal nt1 = getNonterminal("IC");
		Grammar grammar = inputGrammarWithInconsistentRules( nt1 );
		testObject.precomputeReachability(grammar);
		
		assertThat( nt1.reachableTentaclesFrom(0), contains(1) );
		assertThat( nt1.reachableTentaclesFrom(1), contains(0) );
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
		testObject.precomputeReachability(grammar);

		assertThat( nt1.reachableTentaclesFrom(0), contains(1) );
		assertThat( nt1.reachableTentaclesFrom(1), empty() );
		
		assertThat( nt2.reachableTentaclesFrom(0), contains(1) );
		assertThat( nt2.reachableTentaclesFrom(1), empty() );
	}
	
	/**
	 * test using a SLL grammar with indices
	 * SLL[sX] -> .-next->.-SLL[X]-.
	 * SLL[Z] -> .-next->.
	 */
	@Test
	public void testSLLWithIndices() {
		BasicNonterminal nonterminal = getNonterminal("SLL");
		IndexVariable indexVariable = new IndexVariable();

		Nonterminal lhs1 = getIndexedVariableLhs(nonterminal, indexVariable);
		Nonterminal lhs2 = getIndexedBaseLhs(nonterminal);
		Nonterminal ntOnRhs = indexedNtForRhs(nonterminal, indexVariable);
		Grammar grammar = indexedInputGrammar( lhs1, lhs2, ntOnRhs);
		testObject.precomputeReachability(grammar);
		
		assertEquals( 2, grammar.getAllLeftHandSides().size() );

		assertThat( lhs1.reachableTentaclesFrom(0), contains(1) );
		assertThat( lhs1.reachableTentaclesFrom(1), empty() );
		
		assertThat( lhs2.reachableTentaclesFrom(0), contains(1) );
		assertThat( lhs2.reachableTentaclesFrom(1), empty() );
		
		assertThat( ntOnRhs.reachableTentaclesFrom(0), contains(1) );
		assertThat( ntOnRhs.reachableTentaclesFrom(1), empty() );
		
		//verify, that the reachability information also for indices not seen in the grammar is correct.
		Nonterminal extraNt = getNonterminalWithNewIndex( nonterminal );
		assertThat( extraNt.reachableTentaclesFrom(0), contains(1) );
		assertThat( extraNt.reachableTentaclesFrom(1), empty() );
		
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
	
	private Grammar inputGrammarWithInconsistentRules(Nonterminal nt1) {
		
		SelectorLabel right = scene.getSelectorLabel("right");
		SelectorLabel left = scene.getSelectorLabel("left");
		
		HeapConfiguration leftToRight = singlePointerLeftToRight(right);
		HeapConfiguration rightToLeft = singlePointerRightToLeft(left);
		
		return Grammar.builder().addRule(nt1, rightToLeft)
				.addRule(nt1, leftToRight)
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
	private Grammar indexedInputGrammar( Nonterminal variableLhs, Nonterminal baseLhs, Nonterminal ntOnRhs ) {
		SelectorLabel sel = scene.getSelectorLabel("next");
		
		HeapConfiguration recursiveRule = singlePointerToNonterminalEdge(ntOnRhs, sel );
		HeapConfiguration baseRule = singlePointerLeftToRight(sel);
		
	
		return Grammar.builder().addRule(variableLhs, recursiveRule)
								.addRule(baseLhs, baseRule)
								.build();
	}
	private Nonterminal indexedNtForRhs(BasicNonterminal nt, IndexVariable indexVariable) {
		List<IndexSymbol> indexOnRhs = SingleElementUtil.createList( indexVariable );
		Nonterminal ntOnRhs = new IndexedNonterminalImpl(nt, indexOnRhs);
		return ntOnRhs;
	}
	private Nonterminal getIndexedBaseLhs(BasicNonterminal nt) {
		List<IndexSymbol> index2 = SingleElementUtil.createList( ConcreteIndexSymbol.getIndexSymbol("Z", true) );
		Nonterminal lhs2 = new IndexedNonterminalImpl(nt, index2);
		return lhs2;
	}
	private Nonterminal getIndexedVariableLhs(BasicNonterminal nt, IndexVariable indexVariable) {
		List<IndexSymbol> index1 = new ArrayList<>();
		index1.add( ConcreteIndexSymbol.getIndexSymbol("s", false));
		index1.add(indexVariable);
		Nonterminal lhs1 = new IndexedNonterminalImpl(nt, index1);
		return lhs1;
	}
	
	private Nonterminal getNonterminalWithNewIndex( BasicNonterminal nt ) {
		List<IndexSymbol> index = new ArrayList<>();
		index.add( ConcreteIndexSymbol.getIndexSymbol("neu", true));
		Nonterminal indexedNt = new IndexedNonterminalImpl(nt, index);
		return indexedNt;
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
	
	private HeapConfiguration singlePointerRightToLeft(SelectorLabel sel) {
		HeapConfigurationBuilder builder = scene.createHeapConfiguration().builder();

		TIntArrayList nodes = new TIntArrayList();
		Type type = scene.getType("node");
		return builder.addNodes(type, 2, nodes)
				.addSelector(nodes.get(1), sel, nodes.get(0) )
				.setExternal( nodes.get(0) )
				.setExternal( nodes.get(1) )
				.build();
	}

	private BasicNonterminal getNonterminal(String label) {
		return (BasicNonterminal) scene.createNonterminal(label, 2, new boolean[] {false,false} );
	}

}
