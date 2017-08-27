package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import gnu.trove.list.array.TIntArrayList;

public class TestHeapConfigurationBuilder extends InternalHeapConfigurationBuilder {

	public TestHeapConfigurationBuilder(InternalHeapConfiguration heapConf) {
		super(heapConf);
	}
	
	public HeapConfigurationBuilder addNonterminalEdge(Nonterminal label, int desiredId, TIntArrayList attachedNodes) {
		
		if(label == null || attachedNodes == null) {
			throw new NullPointerException();
		}
		
		if(label.getRank() != attachedNodes.size()) {
			throw new IllegalArgumentException("The rank of the provided label and the size of the list of attached nodes do not coincide.");
		}
		
		int privateId = getPrivateId( desiredId );
		
		heapConf.graph.addNode(label, attachedNodes.size(), 0);
		for(int i=0; i < attachedNodes.size(); i++) {
			int to = getPrivateId( attachedNodes.get(i) );
			if(!isNode(to)) {
				throw new IllegalArgumentException("ID of one attached node does not actually correspond to a node.");
			}
			heapConf.graph.addEdge(privateId, new Integer(i), to);
		}
		++heapConf.countNonterminalEdges;
		
		return this;
	}

}
