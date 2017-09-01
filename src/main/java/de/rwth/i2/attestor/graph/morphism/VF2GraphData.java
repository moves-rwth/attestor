package de.rwth.i2.attestor.graph.morphism;

import gnu.trove.list.array.TIntArrayList;


/**
 *
 * Collects all data required by {@link VF2Algorithm} during the search for a suitable matching for
 * a single graph, that is either the pattern graph or the target graph.
 * In particular, this includes the current mapping between the underlying graph and the corresponding other graph.
 * Furthermore, VF2GraphData includes information to prune the search space, such as the number of incoming
 * and outgoing edges to nodes that have not been matched yet.
 *
 * @author Christoph
 */
public class VF2GraphData {

	/**
	 * Placeholder value representing that no node contained in a graph
	 * has been chosen yet.
	 */
	static final int NULL_NODE = -1;

	/**
	 * The {@link Graph} underlying the data stored in this object.
	 */
	private final Graph graph;

	/**
	 * The length of the currently stored partial matching.
	 * This value coincides with the current height of the
	 * search tree constructed so far.
	 */
	private int matchLength;

	/**
	 * The identifier of the last node that has been matched.
	 * This value is used for backtracking.
	 */
	private int lastMatchedNode;

	/**
	 * The number of nodes that have not been matched yet, but are reachable
	 * via an incoming edge from an already matched node.
	 */
	private int terminalInLength;

	/**
	 * The number of nodes that have not been matched yet, but are reachable
	 * via an outgoing edge from an already matched node.
	 */
	private int terminalOutLength;

	/**
	 * The current (partial) mapping from graph to the other considered graph.
	 */
	private int[] match;

	/**
	 * For each node this stores the height of the search tree at which the node
	 * was for the first time not in the matching, but reachable via an incoming edge from the matching.
	 */
	private int[] in;

	/**
	 * For each node this stores the height of the search tree at which the node
	 * was for the first time not in the matching, but reachable via an outgoing edge from the matching.
	 */
	private int[] out;

	/**
	 * A fixed order of nodes.
	 * nodeOrder[i] is the position in the fixed order.
	 */
	private int[] nodeOrder;

	/**
	 * Initializes an empty VF2GraphData object.
	 * @param graph The graph represented by this object.
	 * @param nodeOrder Determines a custom node order or the default order if nodeOrder == null.
	 */
	public VF2GraphData(Graph graph, int[] nodeOrder) {
		
		assert(nodeOrder == null || nodeOrder.length == graph.size());
		
		this.graph = graph;
		
		int noNodes = graph.size();
		
		matchLength = 0;
		lastMatchedNode = NULL_NODE;
		terminalInLength = 0;
		terminalOutLength = 0;
		
		
		match = new int[noNodes];
		in = new int[noNodes];
		out = new int[noNodes];
		
		this.nodeOrder = new int[noNodes];


		for(int i=0; i < noNodes; i++) {
			match[i] = NULL_NODE;
			in[i] = NULL_NODE;
			out[i] = NULL_NODE;

			if(nodeOrder == null) {
				this.nodeOrder[i] = i;
			} else {
				this.nodeOrder[i] = nodeOrder[i];
			}
		}
		
	}

    /**
     * Creates a shallow copy of the given VF2GraphData object.
     * @param data The VF2GraphData object that should be copied.
     */
	public VF2GraphData(VF2GraphData data) {
		graph = data.graph;
		matchLength = data.matchLength;
		lastMatchedNode = data.lastMatchedNode;
		terminalInLength = data.terminalInLength;
		terminalOutLength = data.terminalOutLength;
		
		match = data.match;
		in = data.in;
		out = data.out;
		nodeOrder = data.nodeOrder;
	}

    /**
     * @return The graph underlying this VF2GraphData object.
     */
	public Graph getGraph() {
		return graph;
	}

    /**
     * Checks whether the partial morphism stored for the graph underlying this VF2GraphData
     * contains a matching for the given node.
     * @param node The node that should be checked for a matching node.
     * @return true if and only if the stored morphism already contains the given node.
     */
	public boolean containsMatch(int node) {
		
			return node < match.length
					&& match[node] != NULL_NODE;	
	}

    /**
     * Checks whether the given node is reachable by in ingoing edge from a matched node,
     * but has not been matched itself yet.
     * @param node The node that should be checked.
     * @return true if and only if the stored morphism does not contain the given node yet, but the node is
     *         reachable via an ingoing edge from a matched node.
     */
	public boolean containsIngoing(int node) {
		
			return node < match.length 
					&& in[node] != NULL_NODE
					&& match[node] == NULL_NODE;
	}

    /**
     * Checks whether the given node is reachable by in outgoing edge from a matched node,
     * but has not been matched itself yet.
     * @param node The node that should be checked.
     * @return true if and only if the stored morphism does not contain the given node yet, but the node is
     *         reachable via an outgoing edge from a matched node.
     */
	public boolean containsOutgoing(int node) {
		
		return node < match.length 
				&& out[node] != NULL_NODE
				&& match[node] == NULL_NODE;
	}

    /**
     * Checks whether the given node has not been matched yet, but is reachable via a single edge from a
     * matched node.
     * @param node The node that should be checked.
     * @return true if and only if the stored morphism does not contain the given node yet, but the node is
     *         reachable via a single edge from a matched node.
     */
	public boolean containsNeighbor(int node) {
		
		return node < match.length
				&& match[node] == NULL_NODE
				&& ( in[node] != NULL_NODE 
					|| out[node] != NULL_NODE
					);
	}

    /**
     * @return True if and only if there are no ingoing edges to already matched nodes from nodes that
     *         have not been matched yet.
     */
    boolean isIngoingEmpty() {
		return terminalInLength == 0;
	}

    /**
     * @return True if and only if there are no outgoing edges to already matched nodes from nodes that
     *         have not been matched yet.
     */
    boolean isOutgoingEmpty() {
		return terminalOutLength == 0;
	}

    /**
     * Get the matching node for the given node.
     * If the given node is not part of the partial Morphism stored in this VF2GraphData, this method returns null.
     * @param node The node whose matching node is required.
     * @return The matching node or null if no such node is known yet.
     */
	public int getMatch(int node) {
		return match[node];
	}

    /**
     * Adds a pair of matching nodes by mapping the node matchFrom belonging to the underlying graph
     * to the node matchTo in the other graph.
     * @param matchFrom A node belonging to the underlying graph.
     * @param matchTo A node belonging to the other graph.
     */
    void setMatch(int matchFrom, int matchTo) {
		
		assert(matchFrom != NULL_NODE);
		
		match[matchFrom] = matchTo;
		++matchLength;
		lastMatchedNode = matchFrom;
		
		updateTerminalSets(matchFrom);
	}

    /**
     * Updates the 'terminalSets', that is the number and position in the search tree
     * of nodes that have not been matched yet, but that can be reached via a single ingoing/outgoing edge.
     * @param node The node whose terminal sets should be updated.
     */
	private void updateTerminalSets(int node) {
		
		if(out[node] != NULL_NODE) {
			--terminalOutLength;
		}
		
		TIntArrayList succsOfNode = graph.getSuccessorsOf(node);
		for(int i=0; i < succsOfNode.size(); i++) {
			
			int succ = succsOfNode.get(i);
			if(succ < out.length && out[succ] == NULL_NODE) {
				out[succ] = matchLength;
				
				if(!containsMatch(succ)) {
					++terminalOutLength;	
				}
				
			}
		}
		
		if(in[node] != NULL_NODE) {
			--terminalInLength;
		}
		
		TIntArrayList predsOfNode = graph.getPredecessorsOf(node);
		for(int i=0; i < predsOfNode.size(); i++) {
			
			int pred = predsOfNode.get(i);	
			if(pred < in.length && in[pred] == NULL_NODE) {
				in[pred] = matchLength;
				
				if(!containsMatch(pred)) {
					++terminalInLength;
				}
			}
		}
	}

    /**
     * Backtracks the current state of this VF2GraphData.
     * For the last node that has been matched, an inverse update
     * is applied to its terminal sets.
     * The last matching pair is removed.
     * Note that the last matched node has to be set afterwards again by determining
     * the next candidate pair.
     */
    void backtrack() {
		
		if(lastMatchedNode == NULL_NODE) {
			return;
		}
		
		TIntArrayList succsOf = graph.getSuccessorsOf(lastMatchedNode);
		for(int i=0; i < succsOf.size(); i++) {
			
			int succ = succsOf.get(i);
			if(succ < out.length && out[succ] == matchLength) {
				out[succ] = NULL_NODE;
				
				if(!containsMatch(succ)) {
					--terminalOutLength;
				}
			}
		}
		
		TIntArrayList predsOf = graph.getPredecessorsOf(lastMatchedNode);
		for(int i=0; i < predsOf.size(); i++) {
			
			int pred = predsOf.get(i);
			if(pred < in.length && in[pred] == matchLength) {
				in[pred] = NULL_NODE;
				
				if(!containsMatch(pred)) {
					--terminalInLength;
				}
			}
		}
		
		match[lastMatchedNode] = NULL_NODE;
		--matchLength;
		lastMatchedNode = NULL_NODE; // has to be set by set match again
	}

    /**
     * Checks whether one node has precedence over another according to the internally stored node order.
     * @param node1 A node belonging to the underlying graph.
     * @param node2 Another node belonging to the underlying graph.
     * @return true if and only if the first node has precedence over the second node.
     */
    boolean isLessThan(int node1, int node2) {

        // NULL_NODE is considered to be the largest possible element of the order
        return node1 != NULL_NODE && (node2 == NULL_NODE || nodeOrder[node1] < nodeOrder[node2]);
    }

    /**
     * @return An array determining the currently stored matching from the underlying graph into the other graph.
     */
	public int[] getMatching() {
		return match;
	}

    /**
     * @return The current size (and thus depth of the search tree) of the partial morphism stored in this VF2GraphData.
     */
	public int getMatchingSize() {
		return matchLength;
	}

    /**
     * @return The number of nodes not matched but reachable via an ingoing edge.
     */
	public int getTerminalInSize() {
		return terminalInLength;
	}

    /**
     * @return The number of nodes not matched but reachable via an outgoing edge.
     */
	public int getTerminalOutSize() {
		return terminalOutLength;
	}

    /**
     * @return A String representation of the currently stored partial morphism for debugging purposes.
     */
	public String toString() {
		
		StringBuilder res = new StringBuilder();
		for(int i=0; i < match.length; i++) {
			res.append(String.valueOf(i)).append(":");
			res.append(String.valueOf(match[i]));
			res.append(" ");
		}
		
		return res.toString();
	}
}
