package de.rwth.i2.attestor.ipa;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.*;

import org.junit.Test;

import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class IpaContractCollectionTest {

	private static final SelectorLabel SEL = BasicSelectorLabel.getSelectorLabel("sel");
	Type type = Settings.getInstance().factory().getType("type");

	@Test
	public void test() {
		IpaContractCollection contracts = new IpaContractCollection();
		
		HeapConfiguration h1 = simpleGraph();
		HeapConfiguration h2 = otherSimpleGraphWithSameHash();
		assertEquals( h1.hashCode(), h2.hashCode() );
		
		IpaPrecondition p1 = new IpaPrecondition(h1);
		IpaPrecondition p2 = new IpaPrecondition(h2);
		
		assertFalse("should not contain p1", contracts.hasPrecondition(p1) );
		assertFalse("should not contain p2",  contracts.hasPrecondition( p2 ) );
		assertNull("h1 contract should be null", contracts.getContract( h1 ) );
		assertNull("h2 contract should be null", contracts.getContract( h2 ) );
		
		contracts.addPrecondition( p1 );
		
		assertTrue("should have p1", contracts.hasPrecondition(p1) );
		assertFalse("should not have p2", contracts.hasPrecondition( p2 ) );
		assertNotNull("should have contract h1", contracts.getContract( h1 ) );
		assertEquals("precondition of contract for h1 should be p1", p1, contracts.getContract(h1).getKey() );
		assertThat("postconditon of contract h1 should be empty", contracts.getContract(h1).getValue(), empty() );
		assertNull("should not have contract h2", contracts.getContract( h2 ) );
		
		HeapConfiguration somePostCondition = simpleGraph();
		contracts.getContract(h1).getValue().add(somePostCondition);
		
		assertThat("", contracts.getContract(h1).getValue(), contains( somePostCondition));
		
		
	}

	private HeapConfiguration otherSimpleGraphWithSameHash() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.addSelector(nodes.get(0), SEL,	nodes.get(1))
				.addVariableEdge("x", nodes.get(0))
				.build();	
	}

	private HeapConfiguration simpleGraph() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.addSelector(nodes.get(1), SEL,	nodes.get(0))
				.addVariableEdge("x", nodes.get(0))
				.build();	
	}

}
