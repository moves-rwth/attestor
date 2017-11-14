package de.rwth.i2.attestor.ipa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class IpaAbstractMethod_testReordering {

	IpaAbstractMethod ipa = new IpaAbstractMethod( "testMethod" );
	
	Type type = Settings.getInstance().factory().getType("someType");
	String nonterminalLabel = "IpaAbstractMethodTest";
	
	@Test
	public void test() {
		int [] order1 = new int[]{1,0,2};
		int [] order2 = new int[]{2,1,0};
		
		HeapConfiguration matching = someGraph( order1 );
		HeapConfiguration toMatch = someGraph( order2 );
		
		Pair<HeapConfiguration,Integer> toAdapt = someGraphWithNonterminal( order1 );
		HeapConfiguration expectedAdaptation = someGraphWithNonterminal( order2 ).first();
		
		assertTrue( ipa.contracts.match( toMatch, matching ) );
		int[] reordering = ipa.contracts.getReordering( toMatch, matching );
		assertEquals( expectedAdaptation, ipa.adaptExternalOrdering(matching, toAdapt.first(), toAdapt.second(), reordering) );
	}

	/**
	 * this example occurred similarly in practice
	 */
	@Test
	public void testFiveExternals() {
		int [] externalOrder1 = new int[]{0,1,2,3,4};
		int [] externalOrder2 = new int[]{2,1,3,0,4};
		HeapConfiguration matching = someGraph( externalOrder1 );
		HeapConfiguration toMatch = someGraph( externalOrder2 );
		
		
		int [] tentacleOrder1 = new int[]{0,1,3,4,2};
		int [] tentacleOrder2 = new int[]{3,1,4,0,2};
		Pair<HeapConfiguration,Integer> toAdapt = someGraphWithNonterminal( tentacleOrder1 );
		HeapConfiguration expectedAdaptation = someGraphWithNonterminal( tentacleOrder2 ).first();
		
		assertTrue( ipa.contracts.match( toMatch, matching ) );
		int[] reordering = ipa.contracts.getReordering( toMatch, matching );
		assertEquals( expectedAdaptation, ipa.adaptExternalOrdering( matching, toAdapt.first(), toAdapt.second(), reordering) );
	}
	
	/*
	 * these variables should fix the node ordering. Otherwise the test will
	 * pass trivially by being isomophic to the expected solution.
	 */
	private HeapConfiguration addVariables( HeapConfiguration hc ){
		HeapConfigurationBuilder builder = hc.builder();
		
		TIntArrayList nodes = hc.nodes();
		for( int i = 0; i < nodes.size(); i++ ){
			builder.addVariableEdge("no."+i, nodes.get(i) );
		}
		return builder.build();
	}


	private HeapConfiguration someGraph(int[] orderingOfExternals ) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder()
				.addNodes(type, orderingOfExternals.length, nodes);
				
		for (int i = 0; i < orderingOfExternals.length; i++) {
			builder.setExternal( nodes.get( orderingOfExternals[i]) );
		}
		
		return 	addVariables(builder.build());
	}
	
	private Pair<HeapConfiguration, Integer> someGraphWithNonterminal( int[] orderingOfTentacles ) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder()
				.addNodes(type, orderingOfTentacles.length, nodes);
		
		TIntArrayList tentacles = new TIntArrayList();
		for (int i = 0; i < orderingOfTentacles.length; i++) {
			tentacles.add( nodes.get(orderingOfTentacles[i]) );
		}
		
		int rank = tentacles.size();
		Nonterminal nt = BasicNonterminal.getNonterminal( nonterminalLabel + rank, rank, new boolean[rank]);
		
		int positionOfNonterminal = builder.addNonterminalEdgeAndReturnId( nt, tentacles );
		
		return new Pair<HeapConfiguration, Integer>( addVariables(builder.build()), positionOfNonterminal );
		
	}

}
