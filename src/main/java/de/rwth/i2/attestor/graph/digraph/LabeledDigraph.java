package de.rwth.i2.attestor.graph.digraph;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A LabeledDigraph implements a directed graph with labeled nodes and labeled edges.
 * Nodes have to be labeled with instances of {@link NodeLabel} and are represented by integers.
 * Edges can be labeled with arbitrary objects and are identified by their source node and either
 * their label or their position on the sequence of outgoing edges of a node.
 * Note that multiple edges between two nodes as well as loops are allowed.
 *
 * @author Christoph
 */
public class LabeledDigraph {

    /**
     * Integer representing an invalid element of a LabeledDigraph.
     */
    public static final int INVALID = -1;

    /**
     * The label of each node.
     */
    private final List<NodeLabel> nodeLabels;

    /**
     * List of all successor nodes of each node.
     */
    private final List<TIntArrayList> successors;

    /**
     * List of all predecessor nodes of each node.
     */
    private final List<TIntArrayList> predecessors;

    /**
     * A list of edge labels for each node.
     * The list is ordered in the same way as the list of successors.
     * Hence, {@code successors.get(i).get(j)}
     * refers to the edge labels from the i-th node to its j-th successor node.
     * That is, the node {@code edgeLabels.get(i).get(j)}.
     */
    private final List<List<Object>> edgeLabels;

    /**
     * A list of all "external" (special marked) nodes.
     */
    private final TIntArrayList externalNodes;

    /**
     * Creates a new LabeledDigraph.
     *
     * @param capacity The initial capacity in terms of number of nodes that can be
     *                 stored without increasing the size of underlying data structures.
     */
    public LabeledDigraph(int capacity) {

        nodeLabels = new ArrayList<>(capacity);
        successors = new ArrayList<>(capacity);
        predecessors = new ArrayList<>(capacity);
        edgeLabels = new ArrayList<>();
        externalNodes = new TIntArrayList(capacity);
    }

    /**
     * Creates a deep copy of a given LabeledDigraph.
     *
     * @param digraph The original graph.
     */
    public LabeledDigraph(LabeledDigraph digraph) {

        int size = digraph.nodeLabels.size();

        nodeLabels = new ArrayList<>(digraph.nodeLabels);
        externalNodes = new TIntArrayList(digraph.externalNodes);

        successors = new ArrayList<>(size);
        predecessors = new ArrayList<>(size);
        edgeLabels = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {

            successors.add(new TIntArrayList(digraph.successors.get(i)));
            predecessors.add(new TIntArrayList(digraph.predecessors.get(i)));
            edgeLabels.add(new ArrayList<>(digraph.edgeLabels.get(i)));
        }
    }

    /**
     * @return The number of nodes contained in this LabeledDigraph.
     */
    public int size() {

        return nodeLabels.size();
    }

    /**
     * Adds a new node with the provided label.
     *
     * @param label               The label of the new node.
     * @param successorCapacity   The number of successor nodes that can be added to
     *                            this node without increasing underlying data structures.
     * @param predecessorCapacity The number of predecessor nodes that can be added to this
     *                            this node without increasing underlying data structures.
     */
    public void addNode(NodeLabel label, int successorCapacity, int predecessorCapacity) {

        nodeLabels.add(label);
        successors.add(new TIntArrayList(successorCapacity));
        predecessors.add(new TIntArrayList(predecessorCapacity));
        edgeLabels.add(new ArrayList<>());
    }

    /**
     * @param node The requested node.
     * @return NodeLabel of the given node or null if the provided index does not correspond to a node.
     */
    public NodeLabel nodeLabelOf(int node) {

        if (containsNode(node)) {

            return nodeLabels.get(node);
        } else {
            return null;
        }
    }

    /**
     * @param value The value that should be checked whether it corresponds to a node.
     * @return True if and only if the provided value corresponds to a node.
     */
    public boolean containsNode(int value) {

        return value >= 0 && value < nodeLabels.size() && nodeLabels.get(value) != null;
    }

    /**
     * @param node The node that should be removed.
     * @return True if and only if the given node exists and was successfully removed.
     */
    public boolean removeNodeAt(int node) {

        if (containsNode(node)) {

            removeOccurrences(successors.get(node), predecessors, node);
            removeOccurrences(predecessors.get(node), successors, node);

            nodeLabels.set(node, null);
            successors.set(node, null);
            predecessors.set(node, null);

            //noinspection StatementWithEmptyBody
            while (externalNodes.remove(node)) ;

            return true;
        }

        return false;
    }

    /**
     * Removes all occurrences of the given value from every provided list that
     * is referenced in the list of indices.
     *
     * @param listsToConsider The indices of all the lists in listsOfLists in which
     *                        all occurrences of value should be removed.
     * @param listOfLists     A list of lists in which value should be removed from some lists.
     * @param value           The value that should be removed.
     */
    private void removeOccurrences(TIntArrayList listsToConsider, List<TIntArrayList> listOfLists, int value) {

        for (int i = 0; i < listsToConsider.size(); i++) {

            TIntArrayList l = listOfLists.get(listsToConsider.get(i));
            //noinspection StatementWithEmptyBody
            while (l.remove(value)) ;
        }
    }

    /**
     * Adds a new edge from node from to node to that is labeled with label
     * provided that the two nodes exists and label is not null.
     *
     * @param from  The source node of the new edge.
     * @param label The label of the new edge.
     * @param to    The target of the new edge.
     */
    public void addEdge(int from, Object label, int to) {

        if (containsNode(from) && containsNode(to) && label != null) {

            successors.get(from).add(to);
            predecessors.get(to).add(from);
            edgeLabels.get(from).add(label);
        }

    }

    /**
     * @param node A node in the graph.
     * @return The number of successor nodes of the provided node.
     * If the node is not contained in the graph, 0 is returned.
     */
    public int successorSizeOf(int node) {

        if (containsNode(node)) {
            return successors.get(node).size();
        } else {
            return 0;
        }

    }

    /**
     * @return True if and only if this LabeledDigraph is currently 'packed'.
     * That is, the set of all nodes contained in this graph is a compact
     * interval of the form [0,size()-1].
     */
    public boolean isPacked() {

        for (Object o : nodeLabels) {

            if (o == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param node The node whose predecessor nodes are requested.
     * @return The number of predecessors of the given node.
     * If the node does not belong to this graph the result
     * is 0.
     */
    public int predecessorSizeOf(int node) {

        if (containsNode(node)) {
            return predecessors.get(node).size();
        } else {
            return 0;
        }
    }

    /**
     * @param node A node in the graph.
     * @return A list of all edge labels that occur as labels of edges
     * whose source is the provided node.
     * If node does not belong to this graph the result is null.
     */
    public List<Object> outgoingEdgeLabelsOf(int node) {

        if (containsNode(node)) {
            return edgeLabels.get(node);
        }

        return null;
    }

    /**
     * @param node The node whose outgoing edges are considered.
     *             More precisely, all edges having node as source are considered.
     * @param pos  Ask for the outgoing edge of node at position pos.
     * @return The label of the edge determined by node and pos.
     * If node does not belong to this graph or no edge at position pos exists then
     * null is returned.
     */
    public Object edgeLabelAt(int node, int pos) {

        if (containsNode(node)) {
            List<Object> labels = edgeLabels.get(node);
            if (pos < labels.size()) {
                return labels.get(pos);
            }
        }

        return null;
    }

    /**
     * Removes the outgoing edge of node that is at position pos.
     *
     * @param node The source node whose outgoing edges are considered.
     * @param pos  The position determining which edge should be removed.
     * @return True if and only if the edge could be removed.
     */
    public boolean removeEdgeAt(int node, int pos) {

        if (containsNode(node) && pos < successors.get(node).size()) {

            int to = successors.get(node).get(pos);
            int toPos = predecessorPosOf(to, node);
            predecessors.get(to).removeAt(toPos);

            successors.get(node).removeAt(pos);
            edgeLabels.get(node).remove(pos);

            return true;
        }

        return false;
    }

    /**
     * Find the position of node 'from' in the list of predecessors of node 'to'.
     *
     * @param to   The node whose predecessors are considered.
     * @param from The node whose position in the predecessors of node 'to' should be determined.
     * @return The position of node from in the predecessors of node 'to'.
     * If 'from' is no predecessor of 'to' then -1 is returned.
     */
    private int predecessorPosOf(int to, int from) {

        TIntArrayList pred = predecessors.get(to);
        for (int i = 0; i < pred.size(); i++) {

            if (from == pred.get(i)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Removes the outgoing edge of node 'node' that is labeled with 'label'
     *
     * @param node  The node whose outgoing edges are considered.
     * @param label The label of the edge that should be removed.
     * @return True if and only if the specified edge was found and removed successfully.
     */
    public boolean removeEdgeLabelAt(int node, Object label) {

        int pos = posOfEdgeLabel(node, label);
        return (pos >= 0) && removeEdgeAt(node, pos);
    }

    /**
     * Determines the position of an outgoing edge with the provided
     * label of the provided node.
     *
     * @param node  The node whose outgoing edges are considered.
     * @param label The label of the edge whose position should be determined.
     * @return The position of the outgoing edge of 'node' that is labeled with 'label'
     * or {@link LabeledDigraph#INVALID} if no such edge exists.
     */
    public int posOfEdgeLabel(int node, Object label) {

        List<Object> l = edgeLabels.get(node);
        for (int i = 0; i < l.size(); i++) {

            if (label.equals(l.get(i))) {
                return i;
            }
        }

        return INVALID;
    }

    /**
     * Marks the provided node as external
     *
     * @param node The node that should be marked as external.
     */
    public void setExternal(int node) {

        if (containsNode(node)) {

            externalNodes.add(node);
        }

    }

    /**
     * @return The number of external nodes of this LabeledDigraph.
     */
    public int rank() {

        return externalNodes.size();
    }

    /**
     * Determines the position of a given node in the sequence of external nodes.
     *
     * @param node The node whose position should be determined.
     * @return The position of the node in the sequence of external nodes.
     * If the node does not exist or is not external then {@link LabeledDigraph#INVALID} is returned.
     */
    public int externalPosOf(int node) {

        for (int i = 0; i < externalNodes.size(); i++) {
            if (externalNodes.get(i) == node) {
                return i;
            }
        }

        return INVALID;
    }

    /**
     * @param node The node to be checked.
     * @return True if and only if the given node is marked as external.
     */
    public boolean isExternal(int node) {

        return externalPosOf(node) != INVALID;
    }

    /**
     * @param node The node that should be removed from the sequence of external nodes.
     */
    public void unsetExternal(int node) {

        //noinspection StatementWithEmptyBody
        while (externalNodes.remove(node)) ;
    }

    /**
     * Replaces all outgoing edges of the given node that are labeled with oldLabel
     * by edges labeled with newLabel.
     *
     * @param node     The node whose outgoing edges are considered.
     * @param oldLabel The edge label that should be replaced.
     * @param newLabel The edge label used to replace oldLabel.
     */
    public void replaceEdgeLabel(int node, Object oldLabel, Object newLabel) {

        List<Object> l = edgeLabels.get(node);
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).equals(oldLabel)) {
                l.set(i, newLabel);
            }
        }
    }

    /**
     * @param node  The node whose outgoing edges should be considered.
     * @param label The edge label to look for.
     * @return True if and only if there is an outgoing edge of the given node
     * that is labeled with the provided label.
     */
    public boolean containsEdgeLabel(int node, Object label) {

        return posOfEdgeLabel(node, label) != INVALID;
    }

    /**
     * @param node The node whose predecessors are considered.
     * @return A list of all predecessor nodes of the provided node.
     */
    public TIntArrayList predecessorsOf(int node) {

        if (containsNode(node)) {
            return predecessors.get(node);
        }

        return null;
    }

    /**
     * @param node The node whose successor nodes are considered.
     * @return A list of all successor nodes of the provided node.
     */
    public TIntArrayList successorsOf(int node) {

        if (containsNode(node)) {
            return successors.get(node);
        }

        return null;
    }

    /**
     * Determines a list of all edge labels with source node 'from' and
     * target node 'to'.
     *
     * @param from The source node.
     * @param to   The target node.
     * @return A list of all edge labels of edges with source 'from' and target
     * 'to'.
     */
    public List<Object> edgeLabelsFromTo(int from, int to) {

        if (containsNode(from) && containsNode(to)) {

            List<Object> l = edgeLabels.get(from);
            TIntArrayList succ = successorsOf(from);

            List<Object> res = new ArrayList<>(l.size());
            for (int i = 0; i < l.size(); i++) {
                if (succ.get(i) == to) {
                    res.add(l.get(i));
                }
            }

            return res;
        }

        return null;
    }

    /**
     * Transforms this LabeledDigraph into a more compact form
     * in which the size of the underlying data structures containsSubsumingState
     * no "wholes" due to invalid elements anymore.
     * Whenever an invalid element is encountered it is swapped with the
     * last valid element.
     * After that all invalid elements are removed.
     *
     * @return An array recording all swaps performed.
     */
    public int[] pack() {

        int[] result = new int[nodeLabels.size()];
        Arrays.fill(result, HeapConfiguration.INVALID_ELEMENT);

        removeNullTail();

        for (int i = 0; i < nodeLabels.size() - 1; i++) {

            if (nodeLabels.get(i) == null) {

                int last = nodeLabels.size() - 1;
                move(last, i);
                result[last] = i;

                removeNullTail();
            }
        }

        return result;
    }

    /**
     * Removes all invalid nodes starting from the last node
     * until a valid node is encountered.
     */
    private void removeNullTail() {

        int last = nodeLabels.size() - 1;
        while (last >= 0 && nodeLabels.get(last) == null) {

            nodeLabels.remove(last);
            successors.remove(last);
            predecessors.remove(last);
            edgeLabels.remove(last);
            --last;
        }
    }

    /**
     * Moves a node together with its edges and labels to a new position in the sequence of nodes.
     *
     * @param from The original position of the node to move.
     * @param to   The position to move the node to.
     */
    private void move(int from, int to) {

        replaceAll(externalNodes, from, to);

        TIntArrayList predecessorsCopy = new TIntArrayList(predecessors.get(from));
        for (int i = 0; i < successors.get(from).size(); i++) {

            int s = successors.get(from).get(i);
            replaceAll(predecessors.get(s), from, to);
        }

        for (int i = 0; i < predecessorsCopy.size(); i++) {

            int p = predecessorsCopy.get(i);
            replaceAll(successors.get(p), from, to);
        }

        nodeLabels.set(to, nodeLabels.get(from));
        nodeLabels.set(from, null);

        successors.set(to, successors.get(from));
        successors.set(from, null);

        predecessors.set(to, predecessors.get(from));
        predecessors.set(from, null);

        edgeLabels.set(to, edgeLabels.get(from));
        edgeLabels.set(from, null);
    }

    /**
     * Replaces all occurrences of oldValue by newValue in the provided list.
     *
     * @param list     The list whose elements shall be replaced.
     * @param oldValue The value that should be replaced.
     * @param newValue The value to replace with.
     */
    private void replaceAll(TIntArrayList list, int oldValue, int newValue) {

        if (list == null) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) == oldValue) {
                list.set(i, newValue);
            }
        }
    }

    public int externalNodeAt(int pos) {

        if (0 <= pos && pos < externalNodes.size()) {

            return externalNodes.get(pos);
        }

        return INVALID;
    }

    /**
     * Sets the label of a node to newLabel.
     *
     * @param node     The node.
     * @param newLabel The new label of the node.
     */
    public void replaceNodeLabel(int node, NodeLabel newLabel) {

        nodeLabels.set(node, newLabel);
    }

    /**
     * @return A string representation of this LabeledDigraph.
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodeLabels.size(); i++) {

            sb.append(i);
            sb.append(" : ");
            sb.append(nodeLabels.get(i));
            sb.append("\n\tsuccessors: ");
            sb.append(successors.get(i));
            sb.append("\n\tedge labels: ");
            sb.append(edgeLabels.get(i));
            sb.append("\n\tpredecessors: ");
            sb.append(predecessors.get(i));
            sb.append("\n");
        }
        sb.append("external nodes: ");
        sb.append(externalNodes);

        return sb.toString();
    }

}
