package de.rwth.i2.attestor.ipa;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.junit.Test;

import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.SingleElementUtil;
import gnu.trove.list.array.TIntArrayList;

public class IpaAbstractMethod_getResult {
	
	private static final SelectorLabel sel = BasicSelectorLabel.getSelectorLabel("someLabel");

	IpaAbstractMethod ipa = new IpaAbstractMethod( "testMethod" );
	
	Type type = Settings.getInstance().factory().getType("someType");
	
	@Test
	public void test(){
		IpaPrecondition precondition = createPreCondition();
		HeapConfiguration postcondition = createPostcondition();
		
		ipa.addContracts(precondition, SingleElementUtil.createList(postcondition) );
		
		HeapConfiguration input = createInput();
		HeapConfiguration expected = createExpected();
		
		assertThat( ipa.getResult(input), contains( expected ) );
	}


	private HeapConfiguration createInput() {
	HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		hc = hc.builder()
				.addNodes(type, 3, nodes)
				.addVariableEdge("@parameter0:", nodes.get(0))
				.addVariableEdge("@parameter1:", nodes.get(1))
				.addVariableEdge("x", nodes.get(0))
				.addVariableEdge("y", nodes.get(1))
				.addSelector(nodes.get(1), sel, nodes.get(2))
				.build();
		return hc;
		
	}

	private IpaPrecondition createPreCondition() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		hc = hc.builder()
				.addNodes(type, 3, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.addVariableEdge("@parameter0:", nodes.get(0))
				.addVariableEdge("@parameter1:", nodes.get(1))
				.addSelector(nodes.get(1), sel, nodes.get(2))
				.build();
		
		return new IpaPrecondition( hc );
	}
	
	private HeapConfiguration createPostcondition() {
	HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		hc = hc.builder()
				.addNodes(type, 3, nodes)
				.setExternal(nodes.get(0))
				.setExternal(nodes.get(1))
				.addSelector(nodes.get(1), sel, nodes.get(2))
				.addSelector(nodes.get(0), sel, nodes.get(1))
				.build();
		
		return  hc;
	}
	
	private HeapConfiguration createExpected() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		hc = hc.builder()
				.addNodes(type, 3, nodes)
				.addVariableEdge("x", nodes.get(0))
				.addVariableEdge("y", nodes.get(1))
				.addSelector(nodes.get(1), sel, nodes.get(2))
				.addSelector(nodes.get(0), sel, nodes.get(1))
				.build();
		
		return  hc;
	}

}
