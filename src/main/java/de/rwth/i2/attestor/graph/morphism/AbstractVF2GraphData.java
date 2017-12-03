package de.rwth.i2.attestor.graph.morphism;

import gnu.trove.list.array.TIntArrayList;


/**
 * Collects all data required by {@link VF2Algorithm} during the search for a suitable matching for
 * a single graph.
 *
 * @author Christoph
 */
public abstract class AbstractVF2GraphData {

    /**
     * Placeholder value representing that no node contained in a graph
     * has been chosen yet.
     */
    protected static final int NULL_NODE = -1;

    /**
     * The {@link Graph} underlying the data stored in this object.
     */
    private final Graph graph;
    /**
     * For each node this stores the height of the search tree at which the node
     * was for the first time not in the matching, but reachable via an incoming edge from the matching.
     */
    private final int[] in;
    /**
     * For each node this stores the height of the search tree at which the node
     * was for the first time not in the matching, but reachable via an outgoing edge from the matching.
     */
    private final int[] out;
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
     * Initializes an empty AbstractVF2GraphData object.
     *
     * @param graph The graph represented by this object.
     */
    public AbstractVF2GraphData(Graph graph) {

        this.graph = graph;

        int noNodes = graph.size();

        matchLength = 0;
        lastMatchedNode = NULL_NODE;
        terminalInLength = 0;
        terminalOutLength = 0;


        in = new int[noNodes];
        out = new int[noNodes];

        for (int i = 0; i < noNodes; i++) {
            in[i] = NULL_NODE;
            out[i] = NULL_NODE;
        }

    }

    /**
     * Creates a shallow copy of the given AbstractVF2GraphData object.
     *
     * @param data The AbstractVF2GraphData object that should be copied.
     */
    public AbstractVF2GraphData(AbstractVF2GraphData data) {

        graph = data.graph;
        matchLength = data.matchLength;
        lastMatchedNode = data.lastMatchedNode;
        terminalInLength = data.terminalInLength;
        terminalOutLength = data.terminalOutLength;

        in = data.in;
        out = data.out;
    }

    protected abstract void matchNode(int matchFrom, int matchTo);

    protected abstract void unmatchNode(int node);

    /**
     * Checks whether the partial morphism stored for the graph underlying this AbstractVF2GraphData
     * containsSubsumingState a matching for the given node.
     *
     * @param node The node that should be checked for a matching node.
     * @return true if and only if the stored morphism already containsSubsumingState the given node.
     */
    public abstract boolean containsMatch(int node);

    /**
     * @return The graph underlying this AbstractVF2GraphData object.
     */
    public Graph getGraph() {

        return graph;
    }

    /**
     * Checks whether the given node is reachable by in ingoing edge from a matched node,
     * but has not been matched itself yet.
     *
     * @param node The node that should be checked.
     * @return true if and only if the stored morphism does not contain the given node yet, but the node is
     * reachable via an ingoing edge from a matched node.
     */
    public boolean containsIngoingUnmatched(int node) {

        return in[node] != NULL_NODE
                && !containsMatch(node);
    }

    /**
     * Checks whether the given node is reachable by in outgoing edge from a matched node,
     * but has not been matched itself yet.
     *
     * @param node The node that should be checked.
     * @return true if and only if the stored morphism does not contain the given node yet, but the node is
     * reachable via an outgoing edge from a matched node.
     */
    public boolean containsOutgoingUnmatched(int node) {

        return out[node] != NULL_NODE
                && !containsMatch(node);
    }

    public boolean containsOutgoing(int node) {

        return out[node] != NULL_NODE;
    }

    public boolean containsIngoing(int node) {

        return in[node] != NULL_NODE;
    }

    /**
     * @return True if and only if there are no ingoing edges to already matched nodes from nodes that
     * have not been matched yet.
     */
    boolean isIngoingEmpty() {

        return terminalInLength == 0;
    }

    /**
     * @return True if and only if there are no outgoing edges to already matched nodes from nodes that
     * have not been matched yet.
     */
    boolean isOutgoingEmpty() {

        return terminalOutLength == 0;
    }

    /**
     * Adds a pair of matching nodes by mapping the node matchFrom belonging to the underlying graph
     * to the node matchTo in the other graph.
     *
     * @param matchFrom A node belonging to the underlying graph.
     * @param matchTo   A node belonging to the other graph.
     */
    void setMatch(int matchFrom, int matchTo) {

        matchNode(matchFrom, matchTo);
        ++matchLength;
        lastMatchedNode = matchFrom;

        updateTerminalSets(matchFrom);
    }


    /**
     * Updates the 'terminalSets', that is the number and position in the search tree
     * of nodes that have not been matched yet, but that can be reached via a single ingoing/outgoing edge.
     *
     * @param node The node whose terminal sets should be updated.
     */
    private void updateTerminalSets(int node) {

        if (out[node] != NULL_NODE) {
            --terminalOutLength;
        }

        TIntArrayList succsOfNode = graph.getSuccessorsOf(node);
        for (int i = 0; i < succsOfNode.size(); i++) {

            int succ = succsOfNode.get(i);
            if (out[succ] == NULL_NODE) {
                out[succ] = matchLength;

                if (!containsMatch(succ)) {
                    ++terminalOutLength;
                }

            }
        }

        if (in[node] != NULL_NODE) {
            --terminalInLength;
        }

        TIntArrayList predsOfNode = graph.getPredecessorsOf(node);
        for (int i = 0; i < predsOfNode.size(); i++) {

            int pred = predsOfNode.get(i);
            if (in[pred] == NULL_NODE) {
                in[pred] = matchLength;

                if (!containsMatch(pred)) {
                    ++terminalInLength;
                }
            }
        }
    }

    /**
     * Backtracks the current state of this AbstractVF2GraphData.
     * For the last node that has been matched, an inverse update
     * is applied to its terminal sets.
     * The last matching pair is removed.
     * Note that the last matched node has to be set afterwards again by determining
     * the next candidate pair.
     */
    void backtrack() {

        if (lastMatchedNode == NULL_NODE) {
            return;
        }

        TIntArrayList succsOf = graph.getSuccessorsOf(lastMatchedNode);
        for (int i = 0; i < succsOf.size(); i++) {

            int succ = succsOf.get(i);
            if (out[succ] == matchLength) {
                out[succ] = NULL_NODE;

                if (!containsMatch(succ)) {
                    --terminalOutLength;
                }
            }
        }

        TIntArrayList predsOf = graph.getPredecessorsOf(lastMatchedNode);
        for (int i = 0; i < predsOf.size(); i++) {

            int pred = predsOf.get(i);
            if (in[pred] == matchLength) {
                in[pred] = NULL_NODE;

                if (!containsMatch(pred)) {
                    --terminalInLength;
                }
            }
        }

        unmatchNode(lastMatchedNode);
        --matchLength;
        lastMatchedNode = NULL_NODE; // has to be set by set match again
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
     * @return The current size (and thus depth of the search tree) of the partial morphism stored in this
     * AbstractVF2GraphData.
     */
    public int getMatchingSize() {

        return matchLength;
    }
}
