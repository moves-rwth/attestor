package de.rwth.i2.attestor.ipa;

import static org.junit.Assert.assertEquals;

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

	IpaAbstractMethod ipa = new IpaAbstractMethod( "testMethod", null );
	
	Type type = Settings.getInstance().factory().getType("someType");
	String nonterminalLabel = "IpaAbstractMethodTest";
	
	@Test
	public void test() {
		int [] order1 = new int[]{1,2,0};
		int [] order2 = new int[]{0,2,1};
		IpaPrecondition toMatch = new IpaPrecondition( someGraph( order1 ) );
		HeapConfiguration matching = someGraph( order2 );
		
		Pair<HeapConfiguration,Integer> toAdapt = someGraphWithNonterminal( order1 );
		HeapConfiguration expectedAdaptation = someGraphWithNonterminal( order2 ).first();
		
		assertEquals( toMatch, new IpaPrecondition(matching) );
		assertEquals( expectedAdaptation, ipa.adaptExternalOrdering( toMatch, matching, toAdapt.first(), toAdapt.second()) );
	}



	private HeapConfiguration someGraph(int[] orderingOfExternals ) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		HeapConfigurationBuilder builder =  hc.builder()
				.addNodes(type, orderingOfExternals .length, nodes);
				
		for (int i = 0; i < orderingOfExternals.length; i++) {
			builder.setExternal( nodes.get( orderingOfExternals[i]) );
		}
		
		return 	builder.build();
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
		
		return new Pair<HeapConfiguration, Integer>( builder.build(), positionOfNonterminal );
		
	}

}
