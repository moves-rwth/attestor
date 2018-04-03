package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.digraph.LabeledDigraph;
import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Variable;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.heap.matching.EmbeddingChecker;
import de.rwth.i2.attestor.graph.heap.matching.IsomorphismChecker;
import de.rwth.i2.attestor.graph.heap.matching.MinDistanceEmbeddingChecker;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.MorphismOptions;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.IntPredicate;

/**
 * <p>An implementation of {@link HeapConfiguration} based on bipartite graphs, which are implemented in
 * {@link LabeledDigraph}.
 * In addition to HeapConfiguration, this class also implements {@link Graph}, such that graph morphisms between
 * InternalHeapConfigurations can be computed. See also {@link de.rwth.i2.attestor.graph.morphism}.</p>
 * <p>
 * <p>In an InternalHeapConfiguration, nodes, nonterminal edges and variable edges are represented by nodes in a
 * LabeledDigraph that are distinguished by their node label. Furthermore, selector edges
 * and tentacles between (nonterminal or variable) hyperedges are represented by
 * edges in a LabeledDigraph. Consequently, each selector edge is labeled with a {@link SelectorLabel} and
 * each tentacle is labeled with an int value that represents the tentacles position in the sequence of nodes
 * attached to a hyperedge.
 * To be more precise:</p>
 * <ul>
 * <li>Nodes are labeled with {@link Type}. All incoming and outgoing edges in the underlying LabeledDigraph correspond
 * to selector edges. For each SelectorLabel there is at most one outgoing selector edge for each node.
 * In contrast, a node may have an arbitrary number of incoming selector edges.
 * </li>
 * <li>Nonterminal edges are labeled with {@link Nonterminal}.
 * In the underlying LabeledDigraph a nonterminal edge containsSubsumingState no incoming edges.
 * Moreover, a nonterminal edge with {@link Nonterminal#getRank()} equals to k containsSubsumingState exactly k
 * outgoing edges in the underlying LabeledDigraph that correspond to its tentacles.
 * Thus, these outgoing edges are labeled with integers 0,1,...,k-1, respectively.
 * The target of an outgoing edge labeled with i then corresponds to the i-th node attached to the
 * nonterminal edge.
 * that are labeled with integer values 0,1,...,k-1, respectively.
 * In this setting an edge labeled with i denotes the i-th tentacle of a nonterminal edge.
 * </li>
 * <li>Variable edges are labeled with {@link Variable}.
 * In the underlying LabeledDigraph a variable edge containsSubsumingState no incoming edges.
 * Moreover, each variable edge has exactly one outgoing edge in the underlying LabeledDigraph
 * whose target is the unique node corresponding to the value of the variable.
 * This unique outgoing edge is always labeled with the integer value 0.
 * </li>
 * </ul>
 * <p>
 * <p>The LabeledDigraph underlying an immutable InternalHeapConfiguration is kept compact.
 * This means that {@link LabeledDigraph#size()} equals the number of nodes, nonterminal edges and variable edges.
 * To this end any call to {@link InternalHeapConfigurationBuilder#build()} may swap around elements to remove gaps
 * caused by deleting elements of a HeapConfiguration.
 * Since swapping elements changes their identifiers, InternalHeapConfiguration distinguishes between <b>public</b>
 * and <b>private</b> identifiers:</p>
 * <ul>
 * <li>Private identifiers correspond to the actual identifiers used within the underlying LabeledDigraph.
 * The private identifier of a node in a LabeledDigraph may change, for example after another element has
 * been deleted.
 * Private identifiers are <b>not</b> accessible through the methodExecution provided by {@link HeapConfiguration}.
 * In contrast, all methodExecution provided by {@link Graph} directly use private identifiers to avoid translating
 * between public and private identifiers
 * when computing graph morphisms.
 * </li>
 * <li>Public identifiers provide an additional indirection that remains unaffected by changes to the underlying
 * LabeledDigraph.
 * All methodExecution provided by {@link HeapConfiguration} always take public identifiers as parameters and return
 * public identifiers.
 * </li>
 * </ul>
 * <p> Hence, as long as a client uses only methodExecution provided by HeapConfiguration, all identifiers are public.</p>
 * <p>InternalHeapConfiguration provides the methodExecution {@link InternalHeapConfiguration#getPrivateId(int)}
 * and {@link InternalHeapConfiguration#getPublicId(int)}
 * to translate a public identifier into a private identifier and a private identifier into a public identifier,
 * respectively.
 * </p>
 *
 * @author Christoph
 */
public class InternalHeapConfiguration implements HeapConfiguration, Graph {

    /**
     * The graph structure underlying this InternalHeapConfiguration.
     */
    final LabeledDigraph graph;
    /**
     * A map that ensures that all IDs returned and taken as parameters remain
     * stable -- even in the presence of delete operations.
     * The map thus maps "public IDs" that remain stable and are accessible from outside
     * to "private IDs" that are mutable and only used within this package.
     */
    final TIntIntMap publicToPrivateIDs;
    /**
     * Stores the unique builder if the object is currently mutable.
     * If the object is immutable, builder is set to null.
     */
    HeapConfigurationBuilder builder;
    /**
     * The current number of elements in graph that correspond to nodes.
     */
    int countNodes;
    /**
     * The current number of elements in graph that correspond to variable edges.
     */
    int countVariableEdges;
    /**
     * The current number of elements in graph that correspond to variable edges.
     */
    int countNonterminalEdges;

    TIntSet markedNodes;

    /**
     * Sets up an empty InternalHeapConfiguration.
     */
    public InternalHeapConfiguration() {

        builder = null;
        graph = new LabeledDigraph(10);
        countNodes = 0;
        countVariableEdges = 0;
        countNonterminalEdges = 0;
        publicToPrivateIDs = new TIntIntHashMap(200, 0.5f,
                HeapConfiguration.INVALID_ELEMENT, HeapConfiguration.INVALID_ELEMENT);
    }

    /**
     * Creates a deep copy of an InternalHeapConfiguration.
     *
     * @param hc The InternalHeapConfiguration that should be copied.
     */
    private InternalHeapConfiguration(InternalHeapConfiguration hc) {

        countNodes = hc.countNodes;
        countNonterminalEdges = hc.countNonterminalEdges;
        countVariableEdges = hc.countVariableEdges;
        builder = null;
        graph = new LabeledDigraph(hc.graph);

        publicToPrivateIDs = new TIntIntHashMap(hc.publicToPrivateIDs);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public HeapConfiguration clone() {

        return new InternalHeapConfiguration(this);
    }

    @Override
    public HeapConfiguration getEmpty() {
        return new InternalHeapConfiguration();
    }

    @Override
    public HeapConfigurationBuilder builder() {

        if (builder == null) {
            builder = new InternalHeapConfigurationBuilder(this);
        }

        return builder;
    }

    @Override
    public int countNodes() {

        return countNodes;
    }

    @Override
    public TIntArrayList nodes() {

        return filterElements(countNodes, this::isNode);
    }

    /**
     * @param id A private ID of an element of the HeapConfiguration.
     * @return true if and only if the given element corresponds to a node.
     */
    boolean isNode(int id) {

        return graph.nodeLabelOf(id) instanceof Type;
    }

    /**
     * Creates a list containing the public IDs of all elements of this InternalHeapConfiguration that
     * satisfy the provided predicate.
     *
     * @param capacity        The initial capacity of the returned list.
     * @param filterPredicate A predicate that takes a private ID of an element of this InternalHeapConfiguration
     *                        and returns true if and only if the element should belong to the returned list.
     * @return A list that containsSubsumingState all public IDs whose corresponding private IDs satisfy filterPredicate.
     */
    private TIntArrayList filterElements(int capacity, IntPredicate filterPredicate) {

        TIntArrayList result = new TIntArrayList(capacity);
        for (int i = 0; i < graph.size(); i++) {

            if (filterPredicate.test(i)) {
                result.add(getPublicId(i));
            }
        }

        return result;
    }

    @Override
    public Type nodeTypeOf(int node) {

        int privateId = checkNodeAndGetPrivateId(node);
        return (Type) graph.nodeLabelOf(privateId);
    }

    @Override
    public TIntArrayList attachedVariablesOf(int node) {

        int privateId = checkNodeAndGetPrivateId(node);
        return filterAttachedElements(graph.predecessorsOf(privateId), this::isVariable);
    }

    /**
     * Creates a repetition-free list containing the public IDs of all elements in the provided list that satisfy
     * the provided predicate.
     *
     * @param attached        A list of private IDs of elements belonging to this InternalHeapConfiguration.
     * @param filterPredicate A predicate that takes a private ID of an element of this InternalHeapConfiguration
     *                        and returns true if and only if the element should belong to the returned list.
     * @return A repetition-free list that containsSubsumingState all public IDs whose corresponding private IDs satisfy filterPredicate.
     */
    private TIntArrayList filterAttachedElements(TIntArrayList attached, IntPredicate filterPredicate) {

        TIntArrayList result = new TIntArrayList(attached.size());

        for (int i = 0; i < attached.size(); i++) {
            int a = attached.get(i);
            if (filterPredicate.test(a)) {

                int publicId = getPublicId(a);
                if (!result.contains(publicId)) {
                    result.add(publicId);
                }
            }
        }

        return result;
    }

    @Override
    public TIntArrayList attachedNonterminalEdgesOf(int node) {

        int privateId = checkNodeAndGetPrivateId(node);
        return filterAttachedElements(graph.predecessorsOf(privateId), this::isNonterminalEdge);
    }

    /**
     * Checks whether the provided public ID belongs to this InternalHeapConfiguration.
     * If this is the case it returns the corresponding private ID.
     *
     * @param publicId The public ID to check.
     * @return The private ID corresponding to the provided public ID.
     */
    private int checkNodeAndGetPrivateId(int publicId) {

        int privateId = getPrivateId(publicId);

        if (!isNode(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a node");
        }

        return privateId;
    }

    @Override
    public TIntArrayList successorNodesOf(int node) {

        int privateId = checkNodeAndGetPrivateId(node);
        return filterAttachedElements(graph.successorsOf(privateId), this::isNode);
    }

    @Override
    public TIntArrayList predecessorNodesOf(int node) {

        int privateId = checkNodeAndGetPrivateId(node);
        return filterAttachedElements(graph.predecessorsOf(privateId), this::isNode);
    }

    @Override
    public List<SelectorLabel> selectorLabelsOf(int node) {

        int privateId = checkNodeAndGetPrivateId(node);
        List<SelectorLabel> result = new ArrayList<>();
        List<Object> edgeLabels = graph.outgoingEdgeLabelsOf(privateId);

        if (edgeLabels != null) {
            for (Object obj : edgeLabels) {
                if (obj instanceof SelectorLabel && !result.contains(obj)) {
                    result.add((SelectorLabel) obj);
                }
            }
        }

        return result;

    }

    /**
     * Translates a public ID of an element into the corresponding
     * private ID in the underlying graph.
     *
     * @param publicId A public ID of an element of this HeapConfiguration.
     * @return The private ID corresponding to the given public ID.
     */
    int getPrivateId(int publicId) {

        int res = publicToPrivateIDs.get(publicId);
        if (res != HeapConfiguration.INVALID_ELEMENT) {
            return res;
        }

        throw new IllegalArgumentException("HeapConfiguration does not contain an element with ID: " + publicId);
    }

    @Override
    public int selectorTargetOf(int node, SelectorLabel sel) {

        int privateId = checkNodeAndGetPrivateId(node);
        TIntArrayList succ = graph.successorsOf(privateId);

        for (int i = 0; i < succ.size(); i++) {

            int to = succ.get(i);
            if (isNode(to)) {

                Object label = graph.edgeLabelAt(privateId, i);
                if (sel.equals(label)) {

                    return getPublicId(to);
                }
            }
        }

        return INVALID_ELEMENT;
    }

    @Override
    public int countExternalNodes() {

        return graph.rank();
    }

    @Override
    public TIntArrayList externalNodes() {

        int size = graph.rank();
        TIntArrayList result = new TIntArrayList(size);
        for (int i = 0; i < size; i++) {
            result.add(getPublicId(graph.externalNodeAt(i)));
        }

        return result;
    }

    /**
     * Translates a private ID of an element into the corresponding
     * public ID that is allowed to be returned by methodExecution of this class.
     *
     * @param privateId A private ID of an element of this HeapConfiguration.
     * @return The public ID corresponding to the given private ID.
     */
    int getPublicId(int privateId) {

        TIntIntIterator iter = publicToPrivateIDs.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (iter.value() == privateId) {
                return iter.key();
            }
        }

        throw new IllegalArgumentException("HeapConfiguration does not contain an element with private ID: " + privateId);
    }

    @Override
    public int externalNodeAt(int pos) {

        if (pos < 0 || pos > countExternalNodes()) {
            throw new IllegalArgumentException("The provided position does not specify an external node.");
        }

        return getPublicId(graph.externalNodeAt(pos));
    }

    @Override
    public boolean isExternalNode(int node) {

        int privateId = getPrivateId(node);
        return graph.isExternal(privateId);
    }

    @Override
    public int externalIndexOf(int node) {

        int privateId = checkNodeAndGetPrivateId(node);
        return graph.externalPosOf(privateId);
    }

    @Override
    public int countNonterminalEdges() {

        return countNonterminalEdges;
    }

    @Override
    public TIntArrayList nonterminalEdges() {

        return filterElements(countNonterminalEdges, this::isNonterminalEdge);
    }

    /**
     * Checks whether a given private ID belonging to this InternalHeapConfiguration corresponds to a
     * nonterminal hyperedge.
     *
     * @param privateId A private ID of an element of this InternalHeapConfiguration.
     * @return True if and only if the provided privateId is a nonterminal hyperedge.
     */
    boolean isNonterminalEdge(int privateId) {

        return graph.nodeLabelOf(privateId) instanceof Nonterminal;
    }

    @Override
    public int rankOf(int ntEdge) {

        int privateId = checkNonterminalAndGetPrivateId(ntEdge);

        return graph.successorSizeOf(privateId);
    }

    /**
     * Checks whether the provided public ID belongs to an element of this InternalHeapConfiguration.
     * If this is the case it returns the corresponding private ID.
     *
     * @param publicId A public ID to check.
     * @return The private ID corresponding to the provided public ID.
     */
    private int checkNonterminalAndGetPrivateId(int publicId) {

        int privateId = getPrivateId(publicId);

        if (!isNonterminalEdge(privateId)) {

            throw new IllegalArgumentException("The provided ID does not correspond to a nonterminal edge.");
        }

        return privateId;
    }

    @Override
    public Nonterminal labelOf(int ntEdge) {

        int privateId = checkNonterminalAndGetPrivateId(ntEdge);
        return (Nonterminal) graph.nodeLabelOf(privateId);
    }

    @Override
    public TIntArrayList attachedNodesOf(int ntEdge) {

        int privateId = checkNonterminalAndGetPrivateId(ntEdge);

        TIntArrayList succ = graph.successorsOf(privateId);
        TIntArrayList result = new TIntArrayList(succ.size());

        for (int i = 0; i < succ.size(); i++) {
            int s = succ.get(i);
            result.add(getPublicId(s));
        }

        return result;
    }

    @Override
    public int countVariableEdges() {

        return countVariableEdges;
    }

    @Override
    public TIntArrayList variableEdges() {

        return filterElements(countVariableEdges, this::isVariable);
    }

    @Override
    public int variableWith(String name) {

        for (int i = 0; i < graph.size(); i++) {

            if (isVariable(i) && graph.nodeLabelOf(i).toString().equals(name)) {
                return getPublicId(i);
            }
        }

        return INVALID_ELEMENT;
    }

    /**
     * Checks whether the provided private ID corresponds to a Variable.
     *
     * @param privateId A private ID of an element of this InternalHeapConfiguration.
     * @return True if and only if the provided private ID corresponds to a variable.
     */
    boolean isVariable(int privateId) {

        Object v = graph.nodeLabelOf(privateId);
        return v != null && v.getClass() == Variable.class;
    }

    @Override
    public String nameOf(int varEdge) {

        int privateId = checkVariableAndGetPrivateId(varEdge);

        return graph.nodeLabelOf(privateId).toString();
    }

    /**
     * Checks whether the provided public ID belongs to this InternalHeapConfiguration.
     * If this is the case it returns the corresponding private ID.
     *
     * @param publicId The public ID to check.
     * @return The private ID corresponding to the provided public ID.
     */
    private int checkVariableAndGetPrivateId(int publicId) {

        int privateId = getPrivateId(publicId);

        if (!isVariable(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a variable edge.");
        }
        return privateId;
    }

    @Override
    public int targetOf(int varEdge) {

        int privateId = checkVariableAndGetPrivateId(varEdge);

        return getPublicId(
                graph.successorsOf(privateId).get(0)
        );


    }

    @Override
    public AbstractMatchingChecker getEmbeddingsOf(HeapConfiguration pattern, MorphismOptions morphismOptions) {

        if (morphismOptions.isAdmissibleAbstraction()) {
            return new MinDistanceEmbeddingChecker(pattern, this, morphismOptions);

        } else {
            return new EmbeddingChecker(pattern, this);
        }
    }

    @Override
    public int variableTargetOf(String variableName) {

        int varEdge = variableWith(variableName);
        if (varEdge != HeapConfiguration.INVALID_ELEMENT) {
            return targetOf(varEdge);
        }
        return HeapConfiguration.INVALID_ELEMENT;
    }

    @Override
    public TIntIntMap attachedNonterminalEdgesWithNonReductionTentacle(int node) {

        int privateId = checkNodeAndGetPrivateId(node);
        TIntIntMap result = new TIntIntHashMap(graph.predecessorSizeOf(privateId));
        TIntIterator predIter = graph.predecessorsOf(privateId).iterator();
        while (predIter.hasNext()) {
            int pred = predIter.next();
            if (isNonterminalEdge(pred)) {
                TIntArrayList att = graph.successorsOf(pred);
                Nonterminal label = (Nonterminal) graph.nodeLabelOf(pred);
                for (int tentacle = 0; tentacle < att.size(); tentacle++) {
                    if (att.get(tentacle) == privateId && !label.isReductionTentacle(tentacle)) {
                        result.put(getPublicId(pred), tentacle);
                        break;
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object otherObject) {

        if (otherObject == this) {
            return true;
        }

        if (otherObject == null) {
            return false;
        }

        // otherObject instanceof HeapConfiguration omitted to improve performance
        // Notice that it is *not* sufficient to check classes here.

        HeapConfiguration hc = (HeapConfiguration) otherObject;
        IsomorphismChecker isoChecker = new IsomorphismChecker(this, hc);
        return isoChecker.hasMatching();
    }

    @Override
    public int hashCode() {

        int hash = countNodes();
        hash = (hash << 1) ^ countExternalNodes();
        hash = (hash << 1) ^ countVariableEdges();
        hash = (hash << 1) ^ countNonterminalEdges();

        int variableHash = 0;
        int ntHash = 0;
        int nodeHash = 0;
        for (int i = 0; i < graph.size(); i++) {
            Object label = graph.nodeLabelOf(i);
            int labelHash = label.hashCode();
            if (label.getClass() == Variable.class) {
                variableHash ^= labelHash;
            } else if (label.getClass() == GeneralType.class) {
                ntHash ^= labelHash;
            } else {
                nodeHash ^= labelHash;
            }
        }

        hash = (hash << 1) ^ nodeHash;
        hash = (hash << 1) ^ variableHash;
        hash = (hash << 1) ^ ntHash;

        return hash;
    }

    @Override
    public String toString() {

        StringBuilder res = new StringBuilder("\n");

        res.append(this.nodes().toString());

        res.append("external: ").append(this.externalNodes().toString());
        res.append("\n");
        final TIntArrayList variableEdges = variableEdges();
        StringJoiner variableListString = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < variableEdges.size(); i++) {
            int varId = variableEdges.get(i);
            variableListString.add(nameOf(varId) + "->" + targetOf(varId));
        }
        res.append(variableListString.toString());
        res.append("\n");
        final TIntArrayList nodes = nodes();
        for (int i = 0; i < nodes.size(); i++) {
            int nodeId = nodes.get(i);

            StringJoiner selectorListString = new StringJoiner(", ", nodeId + " -> [", "]");
            for (SelectorLabel sel : selectorLabelsOf(nodeId)) {
                String selectorString = "(" + sel + "," +
                        selectorTargetOf(nodeId, sel) + ")" +
                        " ";
                selectorListString.add(selectorString);
            }
            res.append(selectorListString.toString());
            res.append("\n");
        }
        res.append("\n");

        StringJoiner ntListString = new StringJoiner(", ", "[", "]");
        final TIntArrayList nonterminalEdges = nonterminalEdges();
        for (int i = 0; i < nonterminalEdges.size(); i++) {
            int ntId = nonterminalEdges.get(i);
            ntListString.add(ntId + ":" + labelOf(ntId).toString());
            ntListString.add(attachedNodesOf(ntId).toString());
            ntListString.add("\n");

        }
        res.append(ntListString.toString());
        //res.append( nonterminalEdges.toString() );

        res.append("\n\n");
        return res.toString();
    }


    @Override
    public int size() {

        return graph.size();
    }


    /**
     * This method directly access the {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}
     * underlying an InternalHeapConfiguration through private IDs.
     * It should thus only be used if it can be guaranteed that a HeapConfiguration is immutable.
     * That is, {@link HeapConfiguration#builder()} is {@code null}.
     */
    @Override
    public boolean hasEdge(int privateIdFrom, int privateIdTo) {

        return graph.successorsOf(privateIdFrom).contains(privateIdTo);
    }


    /**
     * This method directly access the {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}
     * underlying an InternalHeapConfiguration through private IDs.
     * It should thus only be used if it can be guaranteed that a HeapConfiguration is immutable.
     * That is, {@link HeapConfiguration#builder()} is {@code null}.
     */
    @Override
    public TIntArrayList getSuccessorsOf(int privateNodeId) {

        return graph.successorsOf(privateNodeId);
    }


    /**
     * This method directly access the {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}
     * underlying an InternalHeapConfiguration through private IDs.
     * It should thus only be used if it can be guaranteed that a HeapConfiguration is immutable.
     * That is, {@link HeapConfiguration#builder()} is {@code null}.
     */
    @Override
    public TIntArrayList getPredecessorsOf(int privateNodeId) {

        return graph.predecessorsOf(privateNodeId);
    }


    /**
     * This method directly access the {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}
     * underlying an InternalHeapConfiguration through private IDs.
     * It should thus only be used if it can be guaranteed that a HeapConfiguration is immutable.
     * That is, {@link HeapConfiguration#builder()} is {@code null}.
     */
    @Override
    public NodeLabel getNodeLabel(int privateNodeId) {

        return graph.nodeLabelOf(privateNodeId);
    }


    /**
     * This method directly access the {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}
     * underlying an InternalHeapConfiguration through private IDs.
     * It should thus only be used if it can be guaranteed that a HeapConfiguration is immutable.
     * That is, {@link HeapConfiguration#builder()} is {@code null}.
     */
    @Override
    public List<Object> getEdgeLabel(int privateIdFrom, int privateIdTo) {

        return graph.edgeLabelsFromTo(privateIdFrom, privateIdTo);
    }


    /**
     * This method directly access the {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}
     * underlying an InternalHeapConfiguration through private IDs.
     * It should thus only be used if it can be guaranteed that a HeapConfiguration is immutable.
     * That is, {@link HeapConfiguration#builder()} is {@code null}.
     */
    @Override
    public int getExternalIndex(int privateNodeId) {

        return graph.externalPosOf(privateNodeId);
    }

    @Override
    public boolean isEdgeBetweenMarkedNodes(int from, int to) {

        if(!isNode(from) ||!isNode(to)) {
            return false;
        }

        if(markedNodes == null) {
            updateMarkedNodes();
        }

        int fromNode = getPublicId(from);
        int toNode = getPublicId(to);

        return markedNodes.contains(fromNode)
                && markedNodes.contains(toNode);
    }

    private void updateMarkedNodes() {

        markedNodes = new TIntHashSet();
        TIntIterator varIterator = variableEdges().iterator();
        while (varIterator.hasNext()) {
            int var = varIterator.next();
            if(Markings.isMarking(nameOf(var))) {
                markedNodes.add(targetOf(var));
            }
        }
    }




    /**
     * This method directly access the {@link de.rwth.i2.attestor.graph.digraph.LabeledDigraph}
     * underlying an InternalHeapConfiguration through private IDs.
     * It should thus only be used if it can be guaranteed that a HeapConfiguration is immutable.
     * That is, {@link HeapConfiguration#builder()} is {@code null}.
     */
    @Override
    public boolean isExternal(int privateNodeId) {

        return graph.isExternal(privateNodeId);
    }


}
