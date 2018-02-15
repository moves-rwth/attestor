package de.rwth.i2.attestor.graph.morphism;

public class VF2GraphData extends AbstractVF2GraphData {

    /**
     * The current (partial) mapping from graph to the other considered graph.
     */
    private final int[] match;

    /**
     * A fixed order of nodes.
     * nodeOrder[i] is the position in the fixed order.
     */
    private final int[] nodeOrder;

    public VF2GraphData(Graph graph) {

        super(graph);

        int noNodes = graph.size();
        match = new int[noNodes];
        nodeOrder = new int[noNodes];


        for (int i = 0; i < noNodes; i++) {
            match[i] = NULL_NODE;
            nodeOrder[i] = i;
        }
    }

    public VF2GraphData(VF2GraphData data) {

        super(data);
        match = data.match;
        nodeOrder = data.nodeOrder;
    }

    @Override
    protected void matchNode(int matchFrom, int matchTo) {

        match[matchFrom] = matchTo;
    }

    @Override
    protected void unmatchNode(int node) {

        match[node] = NULL_NODE;
    }

    @Override
    public boolean containsMatch(int node) {

        return match[node] != NULL_NODE;
    }

    /**
     * Get the matching node for the given node.
     * If the given node is not part of the partial Morphism stored in this AbstractVF2GraphData,
     * this method returns null.
     *
     * @param node The node whose matching node is required.
     * @return The matching node or null if no such node is known yet.
     */
    public int getMatch(int node) {

        return match[node];
    }

    /**
     * Checks whether one node has precedence over another according to the internally stored node order.
     *
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

}
