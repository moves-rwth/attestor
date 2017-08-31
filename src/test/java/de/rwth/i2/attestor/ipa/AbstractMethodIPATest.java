package de.rwth.i2.attestor.ipa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.rwth.i2.attestor.graph.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class AbstractMethodIPATest {

	AbstractMethodIPA ipa = new AbstractMethodIPA( "testMethod", null );


	@Test
	public void testPrepareInput_Simple() {
		HeapConfiguration input = singleNodeHeap();
		HeapConfiguration expectedFragment = singleNodeExternal();
		HeapConfiguration expectedReplace = singleNodeAttached();

		performTest(input, expectedFragment, expectedReplace);
	}

	private void performTest(HeapConfiguration input, HeapConfiguration expectedFragment,
			HeapConfiguration expectedReplace) {
		Pair<HeapConfiguration, HeapConfiguration> result = ipa.prepareInput( input );
		assertEquals("reachable Fragment", expectedFragment, result.first());
		assertEquals("replaced Fragment", expectedReplace, result.second());
	}

	@Test
	public void testPrepareInput_reachableList(){
		for( int size = 2; size < 4; size++ ){
		HeapConfiguration input = reachableList( size );
		HeapConfiguration expectedFragment = reachableList_HeadExternal( size );
		HeapConfiguration expectedReplace = singleNodeAttached();

		performTest( input, expectedFragment, expectedReplace );
		}

	}




	private HeapConfiguration reachableList_HeadExternal(int size) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");
		SelectorLabel nextLabel = BasicSelectorLabel.getSelectorLabel("next");

		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder().addNodes(type, size, nodes)
				.setExternal( nodes.get(0) )
				.addVariableEdge("@parameter0:", nodes.get(0) );
		for( int i = 0; i < size - 1; i++ ){
			builder.addSelector(nodes.get(i), nextLabel, nodes.get(i + 1) );
		}

		return builder.build();
	}

	private HeapConfiguration reachableList( int size) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");
		SelectorLabel nextLabel = BasicSelectorLabel.getSelectorLabel("next");

		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder().addNodes(type, size, nodes)
				.addVariableEdge("@parameter0:", nodes.get(0) );
		for( int i = 0; i < size - 1; i++ ){
			builder.addSelector(nodes.get(i), nextLabel, nodes.get(i + 1) );
		}

		return builder.build();
	}

	private HeapConfiguration singleNodeAttached() {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");
		final int rank = 1;
		final boolean[] isReductionTentacle = new boolean[]{false};
		Nonterminal nt = BasicNonterminal.getNonterminal(ipa.toString() + rank, rank, isReductionTentacle);

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, rank, nodes)
				.addNonterminalEdge(nt)
				.addTentacle(nodes.get(0))
				.build()
				.build();
	}


	private HeapConfiguration singleNodeExternal() {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 1, nodes)
				.setExternal(nodes.get(0))
				.addVariableEdge("@parameter0:", nodes.get(0) )
				.build();
	}


	private HeapConfiguration singleNodeHeap() {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("someType");

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 1, nodes)
				.addVariableEdge("@parameter0:", nodes.get(0) )
				.build();
	}


}
