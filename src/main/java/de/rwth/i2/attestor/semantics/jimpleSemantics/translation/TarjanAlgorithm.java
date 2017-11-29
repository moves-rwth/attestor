package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import java.util.*;

import de.rwth.i2.attestor.ipa.IpaAbstractMethod;

public class TarjanAlgorithm {

	class Vertex{
		public IpaAbstractMethod method;
		public int index = -1;
		public int lowlink = 0;
		public boolean onStack = false;
		
		public Vertex( IpaAbstractMethod method ){
			this.method = method;
		}
	}
	
	List<Vertex> vertices = new ArrayList<>();
	Map<Vertex,Vertex> edges = new HashMap<>();
	
	int index = 0;
	Deque<Vertex> stack = new ArrayDeque<>();
	
	public void markRecursiveMethods(){
		
		for( Vertex v : vertices ){
			if( v.index == -1 ){
				strongconnect(v);
			}
		}
	}

	private void strongconnect(Vertex v) {
		v.index = index;
		v.lowlink = index;
		index++;
		stack.push(v);
		v.onStack = true;
		
		//TODO: https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
		
		
		
	}
	
	
}
