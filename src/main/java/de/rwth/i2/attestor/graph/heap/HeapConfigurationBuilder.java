package de.rwth.i2.attestor.graph.heap;


import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;


/**
 * <p>A HeapConfigurationBuilder allows to mutate a given {@link HeapConfiguration}.
 * Hence, as long as a HeapConfigurationBuilder exists for a HeapConfiguration,
 * the HeapConfiguration is mutable.
 * It is set to an immutable HeapConfiguration again by calling
 * {@link HeapConfigurationBuilder#build()}.</p>
 * <p>
 * <p>There is always at most one HeapConfigurationBuilder for each HeapConfiguration.</p>
 *
 * @author Christoph
 */
public interface HeapConfigurationBuilder {

    /**
     * Marks the underlying HeapConfiguration as immutable again (until a new builder is requested).
     * This method also removes the current builder.
     * It may additionally optimize the internal structure of a HeapConfiguration before making it immutable.
     *
     * @return The underlying HeapConfiguration
     */
    HeapConfiguration build();

    /**
     * Add count nodes of Type type to the underlying graph.
     * The IDs of the added nodes are stored in the provided buffer.
     *
     * @param type   Type of the nodes to be created.
     * @param count  The number of nodes to be created.
     * @param buffer A list in which all created nodes are added.
     * @return The originally used HeapConfigurationBuilder.
     */
    HeapConfigurationBuilder addNodes(Type type, int count, TIntArrayList buffer);

    /**
     * Removes an isolated node from the underlying HeapConfiguration
     *
     * @param node ID of the node to remove
     * @return the builder
     */
    HeapConfigurationBuilder removeIsolatedNode(int node);

    /**
     * Removes the given node from the underlying HeapConfiguration.
     * This method also removes all edges attached to the node.
     *
     * @param node ID of the node to remove
     * @return the builder
     */
    HeapConfigurationBuilder removeNode(int node);


    /**
     * Adds a new selector edge that is labeled with 'sel' and points from node 'from' to node 'to'.
     *
     * @param from source of the selector edge
     * @param sel  label of the selector edge
     * @param to   target of the selector edge
     * @return the builder
     */
    HeapConfigurationBuilder addSelector(int from, SelectorLabel sel, int to);

    /**
     * Removes an outgoing selector edge with the provided label 'sel' from the node 'node'.
     * Nothing happens if the selector does not exist.
     *
     * @param node The node whose selector edges are taken into account.
     * @param sel  The label of the selector edge that should be deleted.
     * @return the builder
     */
    HeapConfigurationBuilder removeSelector(int node, SelectorLabel sel);

    /**
     * Replaces the original label of an outgoing selector edge with label 'oldSel' of node 'node' by
     * the selector label 'newSel'.
     * Nothing happens if no such selector edge exists.
     *
     * @param node   The node whose selector edges are taken into account.
     * @param oldSel The original label of the selector edge to be replaced.
     * @param newSel The new label of the selector edge to be replaced.
     * @return the builder
     */
    HeapConfigurationBuilder replaceSelector(int node, SelectorLabel oldSel, SelectorLabel newSel);

    /**
     * Marks a node as external.
     *
     * @param node The (not external yet) not to mark.
     * @return the builder
     */
    HeapConfigurationBuilder setExternal(int node);

    /**
     * Marks an external node as not external.
     *
     * @param node The external node.
     * @return the builder
     */
    HeapConfigurationBuilder unsetExternal(int node);

    /**
     * Adds a new, not already existing, variable edge and attaches it to the given target node.
     *
     * @param name   Name of the new variable.
     * @param target The node the new variable edge is attached to.
     * @return the builder
     */
    HeapConfigurationBuilder addVariableEdge(String name, int target);

    /**
     * Removes the provided variable edge.
     *
     * @param varEdge The variable edge that should be removed.
     * @return the builder
     */
    HeapConfigurationBuilder removeVariableEdge(int varEdge);

    /**
     * Adds a new nonterminal edge.
     *
     * @param label         The label of the new edge.
     * @param attachedNodes The list of nodes attached to the new edge.
     *                      The size of this list must coincide with the rank of label.
     * @return the builder
     */
    HeapConfigurationBuilder addNonterminalEdge(Nonterminal label, TIntArrayList attachedNodes);

    /**
     * Adds a new nonterminal edge and returns the respective id
     *
     * @param label         The label of the new edge.
     * @param attachedNodes The list of nodes attached to the new edge.
     *                      The size of this list must coincide with the rank of label.
     * @return the resulting id of the new nonterminal edge
     */
    int addNonterminalEdgeAndReturnId(Nonterminal label, TIntArrayList attachedNodes);

    /**
     * gets a nonterminalEdgeBuilder that allows to specify the tentacles of this nonterminal
     * and add the resulting edge to the graph
     *
     * @param nt the nonterminal
     * @return a builder for the given nonterminal and graph
     */
    NonterminalEdgeBuilder addNonterminalEdge(Nonterminal nt);

    /**
     * Removes a given nonterminal edge.
     *
     * @param ntEdge The nonterminal edge to remove.
     * @return the builder
     */
    HeapConfigurationBuilder removeNonterminalEdge(int ntEdge);

    /**
     * Substitutes the label of a given nonterminal edge by the provided label.
     * The rank of the new label has to coincide with the rank of the original label.
     *
     * @param ntEdge The nonterminal edge whose label should be changed.
     * @param newNt  The new nonterminal label.
     * @return the builder
     */
    HeapConfigurationBuilder replaceNonterminal(int ntEdge, Nonterminal newNt);

    /**
     * Performs hyperedge replacement for the given nonterminal edge and the given HeapConfiguration.
     * The rank of ntEdge, i.e. the number of nodes attached to it, and the number of external nodes
     * of replacement have to coincide.
     *
     * @param ntEdge      The nonterminal edge that should be replaced.
     * @param replacement The HeapConfiguration that should replace the hyperedge.
     * @return the builder
     */
    HeapConfigurationBuilder replaceNonterminalEdge(int ntEdge, HeapConfiguration replacement);

    /**
     * Replaces the graph captured by the provided matching by a fresh nonterminal edge labeled with the provided nonterminal.
     * The rank of the nonterminal and the number of external nodes of the pattern graph contained in the matching
     *
     * @param matching    A matching of a pattern graph in the current graph.
     * @param nonterminal The label of the nonterminal edge to be created.
     * @return the builder
     */
    HeapConfigurationBuilder replaceMatching(Matching matching, Nonterminal nonterminal);

    /**
     * Sets the node type of the given node to newType.
     *
     * @param node    The node whose type should be changed.
     * @param newType The new type assigned to the node.
     * @return the builder
     */
    HeapConfigurationBuilder replaceNodeType(int node, Type newType);

    /**
     *
     * Replaces the graph captured by the provided matching by a fresh nonterminal edge labeled with the provided nonterminal.
     * The rank of the nonterminal and the number of external nodes of the pattern graph contained in the matching
     * do not have to coincide as long as the rank matches the size provided externalIndicesMap.
     * In this case, additional tentacles will be added according to that map.
     *
     * @param matching    A matching of a pattern graph in the current graph.
     * @param nonterminal The label of the nonterminal edge to be created.
     * @param externalIndicesMap
     * @return the builder
     */
    HeapConfigurationBuilder replaceMatchingWithCollapsedExternals(Matching matching,
                                                                   Nonterminal nonterminal,
                                                                   TIntArrayList externalIndicesMap);

    /**
     * Merges external nodes according to the provided index map.
     * @param extIndicesMap A mapping from external node positions in the current graph to external node positions
     *                      in the resulting graph, e.g. {0,1,2,0} in order to merge the first and last node.
     * @return the builder
     */
    HeapConfigurationBuilder mergeExternals(TIntArrayList extIndicesMap);
}
