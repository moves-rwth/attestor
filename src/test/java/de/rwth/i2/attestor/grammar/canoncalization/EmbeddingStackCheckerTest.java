package de.rwth.i2.attestor.grammar.canoncalization;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingStackChecker;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalMatching;
import de.rwth.i2.attestor.graph.morphism.Morphism;
import de.rwth.i2.attestor.indexedGrammars.BalancedTreeGrammar;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackVariable;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class EmbeddingStackCheckerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		HeapConfiguration toAbstract = getSimpleInput();
		HeapConfiguration pattern = getSimpleInput();
		Nonterminal lhs = getNonterminal();
		Morphism morphism = getSimpleMorphism();
		Matching embedding = new InternalMatching(pattern, morphism, toAbstract);
		
		EmbeddingStackChecker checker = new EmbeddingStackChecker( embedding, lhs );
		
		assertTrue( checker.canMatch() );
		assertEquals( getSimpleInput, checker.getMaterializedToAbstract() );
		assertEquals( getNonterminal(), checker.getInstantiatedLhs() );
	}

	private Nonterminal getNonterminal() {
		String label = "EmbeddingStackChecker";
		int rank = 2;
		boolean[] isReductionTentacle = new boolean [rank];
		List<StackSymbol> stack = new ArrayList<>();
		stack.add( StackVariable.getGlobalInstance() );
		IndexedNonterminal nt = new IndexedNonterminal(label,rank,isReductionTentacle,stack);
		return nt;
	}

	private HeapConfiguration getSimpleInput() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = BalancedTreeGrammar.TYPE;
		SelectorLabel label = GeneralSelectorLabel.getSelectorLabel("label");

	
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addSelector(nodes.get(0), label, nodes.get(1))
				.build();
	}

}
