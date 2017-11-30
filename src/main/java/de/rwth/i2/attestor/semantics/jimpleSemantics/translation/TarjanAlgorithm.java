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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((method == null) ? 0 : method.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Vertex other = (Vertex) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (!method.equals(other.method))
				return false;
			return true;
		}

		private TarjanAlgorithm getOuterType() {
			return TarjanAlgorithm.this;
		}
	}
	
	Map<IpaAbstractMethod, Vertex> methodToVertex = new HashMap<>();
	List<Vertex> vertices = new ArrayList<>();
	Map<Vertex,List<Vertex>> edges = new HashMap<>();
	
	public void addMethodAsVertex( IpaAbstractMethod method ) {
		Vertex vertex = new Vertex( method );
		assert( ! this.vertices.contains( vertex) );
		this.vertices.add( vertex);
		this.methodToVertex.put(method, vertex);
		edges.put(vertex, new ArrayList<>() );
	}
	
	public void addCallEdge( IpaAbstractMethod caller, IpaAbstractMethod callee ) {
		Vertex v = methodToVertex.get(caller);
		Vertex u = methodToVertex.get(callee);
		edges.get(v).add(u);
	}
	
	int index = 0;
	Deque<Vertex> stack = new ArrayDeque<>();
	
	public void markRecursiveMethods(){
		
		for( Vertex v : vertices ){
			if( v.index < 0 ){
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
		
		for( Vertex w : edges.get(v) ) {
			if( v.index < 0 ) {
				strongconnect( w );
				v.lowlink  = Math.min(v.lowlink, w.lowlink);
			}
		}
		
		if( v.lowlink == v.index ) {
			List<Vertex> scc = new ArrayList<>();
			Vertex w;
			do {
				w = stack.pop();
				w.onStack = false;
				scc.add(w);
			}while( w != v );
			
			if( scc.size() > 1 ) {
				for( Vertex s : scc ) {
					s.method.markAsRecursive();
				}
			}else {
				Vertex s = scc.get(0);
				if( edges.get(s).contains(s) ) { //selfloop
					s.method.markAsRecursive();
				}
			}
		}
		
		
		
	}
	
	
}
