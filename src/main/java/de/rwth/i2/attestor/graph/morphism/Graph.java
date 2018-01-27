package de.rwth.i2.attestor.graph.morphism;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;

/**
 * A model of directed graphs with node and edge labels that is used by {@link MorphismChecker}.
 *
 * @author Christoph
 */
public interface Graph {

    /**
     * @return The total number of nodes if this Graph.
     */
    int size();

    /**
     * @param from The source node of the desired edges.
     * @param to   The target node of the desired edges.
     * @return True if and only if there exists at least
     * one edge with source from and target to.
     */
    boolean hasEdge(int from, int to);

    /**
     * @param node The source node whose successor nodes are requested.
     * @return A list of all successor nodes of the given source node.
     */
    TIntArrayList getSuccessorsOf(int node);

    /**
     * @param node The target node whose predecessor nodes are requested.
     * @return A list of all predecessor nodes of the given target node.
     */
    TIntArrayList getPredecessorsOf(int node);

    /**
     * @param node The node whose label is requested.
     * @return The label of the node or null if the node does not exist.
     */
    NodeLabel getNodeLabel(int node);

    /**
     * @param from The source node.
     * @param to   The target node.
     * @return A repetition-free list of all edge labels of edges whose source node is from
     * and whose target node is to.
     */
    List<Object> getEdgeLabel(int from, int to);

    /**
     * @param node The node that should be checked.
     * @return True if and only if the given node is external.
     */
    boolean isExternal(int node);

    /**
     * @param node An external node.
     * @return The unique position of the node in the sequence of external nodes
     * or -1 if the given node does not exist or is not external.
     */
    int getExternalIndex(int node);

    boolean isEdgeBetweenMarkedNodes(int from, int to);
}
