package de.rwth.i2.attestor.graph.heap;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.morphism.MorphismOptions;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;

import java.util.List;

/**
 * A HeapConfiguration is a hypergraph in which each node corresponds to a location on the
 * heap of a program, such as null or a reference to some object.
 * Nodes are assigned a {@link Type} which determines the class of the object represented by a node.
 * Nodes are accessed through integer values.
 * <p>
 * There are three possible kinds of hyperedges:
 * <ol>
 * <li>
 * Selector edges connect exactly two nodes and represent a pointer from the source node
 * to the target point. Selector edges are labeled with a {@link SelectorLabel}.
 * If a node represents an object of a certain class then a selector edge can be thought of as a
 * variable of that object that references another object. Consequently, the SelectorLabel corresponds
 * to the name of the variable.
 * For each node and each SelectorLabel, there is at most one selector edge having this node as a source node
 * with that SelectorLabel.
 * Selector edges are accessed through their source node and their SelectorLabel.
 * </li>
 * <li>
 * Nonterminal edges connect an arbitrary number of nodes and represent abstract heap shapes, such as
 * linked lists, trees etc. Nonterminal edges are labeled with a {@link Nonterminal} that determines
 * the number of nodes connected to it and what is actually modeled by a nonterminal edge.
 * Nonterminal edges are accessed through integer values.
 * </li>
 * <li>
 * Variables edges model a program variable and are attached to exactly one node.
 * They are labeled with a {@link Variable}.
 * Variable edges are accessed through integer values.
 * </li>
 * </ol>
 * <p>
 * HeapConfigurations can be treated as immutable objects as long as they are not marked as mutable
 * explicitly by calling {@link HeapConfiguration#builder()}.
 *
 * @author Christoph
 */
public interface HeapConfiguration {

    /**
     * Value representing an element that does not belong to any HeapConfiguration.
     */
    int INVALID_ELEMENT = -1;

    /**
     * @return A deep copy of this HeapConfiguration.
     */
    HeapConfiguration clone();

    /**
     * @return An empty heap configuration
     */
    HeapConfiguration getEmpty();


    /**
     * Mark a HeapConfiguration as temporarily mutable by a {@link HeapConfigurationBuilder}.
     * The HeapConfiguration stays mutable until {@link HeapConfigurationBuilder#build} is called.
     * For each HeapConfiguration, there is either no builder (immutable) or exactly one builder (mutable).
     *
     * @return The HeapConfigurationBuilder that allows mutation of this HeapConfiguration.
     */
    HeapConfigurationBuilder builder();


    /**
     * @return The number of nodes contained in the HeapConfiguration.
     */
    int countNodes();

    /**
     * @return A list of all nodes contained in the heap contained in the HeapConfiguration.
     */
    TIntArrayList nodes();

    /**
     * @param node A node contained in the HeapConfiguration.
     * @return The type of the given node.
     */
    Type nodeTypeOf(int node);

    /**
     * @param node A node contained in the HeapConfiguration.
     * @return A list of all variables attached to the given node.
     */
    TIntArrayList attachedVariablesOf(int node);

    /**
     * @param node A node contained in the HeapConfiguration.
     * @return A list of all nonterminal edges attached to the given node.
     */
    TIntArrayList attachedNonterminalEdgesOf(int node);

    /**
     * @param node A node contained in the HeapConfiguration.
     * @return A list of all nodes reachable by a single selector edge from the given node.
     */
    TIntArrayList successorNodesOf(int node);

    /**
     * @param node A node contained in the HeapConfiguration.
     * @return A list of all nodes that can reach the given node by a single selector.
     */
    TIntArrayList predecessorNodesOf(int node);

    /**
     * @param node A node contained in the HeapConfiguration.
     * @return A list of all selectors that occur as edge labels for the given node.
     */
    List<SelectorLabel> selectorLabelsOf(int node);

    /**
     * @param node A node contained in the HeapConfiguration.
     * @param sel  A {@link SelectorLabel} that occurs as an edge label for the given node.
     * @return The node reached by taken the given selector from the given node.
     */
    int selectorTargetOf(int node, SelectorLabel sel);

    /**
     * @return The number of external nodes in this HeapConfiguration.
     */
    int countExternalNodes();

    /**
     * @return A list of all external nodes in this HeapConfiguration.
     */
    TIntArrayList externalNodes();

    /**
     * @param pos An index of an external node satisfying {@code 0 <= pos && pos <= countExternalNodes()}.
     * @return The requested external node.
     */
    int externalNodeAt(int pos);

    /**
     * @param node A node in the HeapConfiguration.
     * @return true if and only if the given node is external.
     */
    boolean isExternalNode(int node);

    /**
     * @param node A node in the HeapConfiguration.
     * @return The position of the given node in the sequence of external nodes.
     */
    int externalIndexOf(int node);

    /**
     * @return The number of nonterminal edges in this HeapConfiguration.
     */
    int countNonterminalEdges();

    /**
     * @return A list of all nonterminal edges in this HeapConfiguration.
     */
    TIntArrayList nonterminalEdges();

    /**
     * @param ntEdge A nonterminal edge in this HeapConfiguration.
     * @return The number of nodes attached to the given nonterminal edge.
     */
    int rankOf(int ntEdge);

    /**
     * @param ntEdge A nonterminal edge in this HeapConfiguration.
     * @return The label of the given nonterminal edge.
     */
    Nonterminal labelOf(int ntEdge);

    /**
     * @param ntEdge A nonterminal edge in this HeapConfiguration.
     * @return The list of nodes attached to the given nonterminal edge.
     */
    TIntArrayList attachedNodesOf(int ntEdge);

    /**
     * @return The number of variable edges in this HeapConfiguration.
     */
    int countVariableEdges();

    /**
     * @return A list of all variable edges contained in this HeapConfiguration.
     */
    TIntArrayList variableEdges();

    /**
     * @param name The name of the requested variable
     * @return The variable with the requested name or {@link HeapConfiguration#INVALID_ELEMENT}.
     */
    int variableWith(String name);

    /**
     * @param varEdge A variable edge in this HeapConfiguration.
     * @return The name of the given variable edge.
     */
    String nameOf(int varEdge);

    /**
     * @param varEdge A variable edge in this HeapConfiguration.
     * @return The node attached to the given variable edge.
     */
    int targetOf(int varEdge);


    /**
     * Returns an AbstractMatchingChecker to search for all occurrences of the given pattern HeapConfiguration
     * in this HeapConfiguration (the target).
     *
     * @param pattern The HeapConfiguration to search for.
     * @param options Options guiding how embeddings are computed.
     * @return An AbstractMatchingChecker to iterate through all found embeddings.
     */
    AbstractMatchingChecker getEmbeddingsOf(HeapConfiguration pattern, MorphismOptions options);

    /**
     * @param variableName The name of the requested variable.
     * @return The unique node attached to a variable edge with the given name or INVALID_ELEMENT if no such
     * variable edge exists.
     */
    int variableTargetOf(String variableName);

    /**
     * @param node A node in the graph.
     * @return Mapping from attached nonterminal edges to node to the index of the first non reduction tentacle attached
     * to node.
     */
    TIntIntMap attachedNonterminalEdgesWithNonReductionTentacle(int node);
}
