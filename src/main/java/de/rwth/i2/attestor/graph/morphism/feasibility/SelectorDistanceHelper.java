package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.types.GeneralType;
import gnu.trove.list.array.TIntArrayList;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * A utility class that computes the distances of all nodes in a Graph to a given node.
 *
 * @author Christoph
 *
 */
class SelectorDistanceHelper {

	/**
	 * Computes the distances -- the number of directed edges required to reach a node from another one -- 
	 * of all nodes in the given graph to the given node.
	 * It essentially uses Dijkstra's shortest path algorithm.
	 * @param graph The graph under consideration.
	 * @param id An identifier of a node belonging to graph.
	 * @return A list in which the n-th entry contains the distance of the n-th node of graph to the node with identifier id.
	 */
	static TIntArrayList getSelectorDistances(Graph graph, int id) {
		
		int size = graph.size();
		
		TIntArrayList dist = new TIntArrayList(size);
		
		PriorityQueue<Integer> queue = initQueue(id, size, dist);
		
		while(!queue.isEmpty()) {
			int u = queue.poll();
			
			TIntArrayList succ = graph.getSuccessorsOf(u);
			for(int i=0; i < succ.size(); i++) {
				int v = succ.get(i);
				if(graph.getNodeLabel(v).getClass() == GeneralType.class) {
					int upd = dist.get(u) + 1;
					if(upd < dist.get(v)) {
						dist.set(v, upd);
						queue.remove(v);
						queue.add(v);
					}
				}
			}
		}
		
		return dist;
		
	}


	/**
	 * Initializes a priority queue of distances of nodes in a graph to a fixed node.
	 * @param id The ID of the node that acts as origin for all distances computed.
	 *           Hence, this node has distance zero.
	 * @param size The number of all nodes in the graph.
	 * @param dist A list that is filled with a trivial overapproximation of distances.
	 * @return A PriorityQueue that is initialized as required by Dijkstra's shortest path algorithm.
	 */
	private static PriorityQueue<Integer> initQueue(int id, int size, TIntArrayList dist) {
		
		PriorityQueue<Integer> queue = new PriorityQueue<>(size, Comparator.comparingInt(dist::get));

		for(int i=0; i < size; i++) {
			if(i == id) {
				dist.add(0);
			} else {
				dist.add(size);
			}
			queue.add(i);
		}
		
		return queue;
	}

}
