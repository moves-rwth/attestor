package de.rwth.i2.attestor.grammar.canoncalization;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.graph.GeneralNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.*;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

public class DefaultMatchingReplacerTest {

	private static final int RANK = 5;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		MatchingReplacer matchingReplacer = new DefaultMatchingReplacer();
		
		Nonterminal lhs = getNonterminal();
		HeapConfiguration graph = getGraph();
		HeapConfiguration rhs = getPattern();
		Matching matching = graph.getEmbeddingsOf( rhs ).getNext();
		HeapConfiguration res = matchingReplacer.replaceIn( graph, lhs, matching );
		
		HeapConfiguration expected = getExpected();
		assertEquals( expected, res );
	}


	private Nonterminal getNonterminal() {
		boolean [] reductionTentacles = new boolean[RANK];
		return GeneralNonterminal.getNonterminal("some label", RANK, reductionTentacles );
	}

	private HeapConfiguration getGraph() {
		 HeapConfiguration hc = new InternalHeapConfiguration();
		 
		 Type type = TypeFactory.getInstance().getType("type");
		 TIntArrayList nodes = new TIntArrayList();
		 return hc.builder().addNodes(type, RANK, nodes).build();
	}

	private HeapConfiguration getPattern() {
		 HeapConfiguration hc = new InternalHeapConfiguration();
		 
		 Type type = TypeFactory.getInstance().getType("type");
		 TIntArrayList nodes = new TIntArrayList();
		 HeapConfigurationBuilder builder =  hc.builder().addNodes(type, RANK, nodes);
		 for( int i = 0; i < RANK; i++ ){
			 builder.setExternal( nodes.get(i) );
		 }
		 return builder.build();
	}

	private HeapConfiguration getExpected() {
		 HeapConfiguration hc = new InternalHeapConfiguration();
		 
		 Type type = TypeFactory.getInstance().getType("type");
		 TIntArrayList nodes = new TIntArrayList();
		 NonterminalEdgeBuilder builder =  hc.builder().addNodes(type, RANK, nodes)
				 .addNonterminalEdge( getNonterminal() ); 
		 for( int i = 0; i < RANK; i++ ){
			 builder.addTentacle( nodes.get(i) );
		 }
		 return builder.build().build();
	}
}
