package de.rwth.i2.attestor.refinement.reachability;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.settings.FactorySettings;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public class ReachabilityHeapAutomaton implements HeapAutomaton {

    @Override
    public HeapAutomatonState transition(HeapConfiguration heapConfiguration,
                                         List<HeapAutomatonState> statesOfNonterminals) {

        HeapConfiguration canonicalHc = computeCanonicalHc(heapConfiguration, statesOfNonterminals);
        HeapConfiguration kernel = computeKernel(canonicalHc);

        return new ReachabilityAutomatonState(kernel);
    }

    private HeapConfiguration computeCanonicalHc(HeapConfiguration heapConfiguration,
                                                 List<HeapAutomatonState> statesOfNonterminals) {

        heapConfiguration = heapConfiguration.clone();
        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        for(int i=0; i < statesOfNonterminals.size(); i++) {
            ReachabilityAutomatonState state = (ReachabilityAutomatonState) statesOfNonterminals.get(i);
            int edge = ntEdges.get(i);
            heapConfiguration.builder().replaceNonterminalEdge(edge, state.kernel);
        }
        return heapConfiguration.builder().build();
    }

    private HeapConfiguration computeKernel(HeapConfiguration canonicalHc) {

        ReachabilityHelper reachabilityHelper = new ReachabilityHelper(canonicalHc);
        FactorySettings factory = Settings.getInstance().factory();

        HeapConfigurationBuilder builder = factory.createEmptyHeapConfiguration().builder();
        Type type = factory.getType("kernelNode");
        int varCount = canonicalHc.countVariableEdges();

        if(varCount == 0) {
            int rank = canonicalHc.countExternalNodes();
            TIntArrayList nodes = new TIntArrayList(rank);
            builder.addNodes(type, rank, nodes);
            for (int i = 0; i < rank; i++) {
                builder.setExternal(i);
                int from = canonicalHc.externalNodeAt(i);
                for (int j = 0; j < rank; j++) {
                    int to = canonicalHc.externalNodeAt(j);
                    if (reachabilityHelper.isReachable(from, to)) {
                        builder.addSelector(nodes.get(i), factory.getSelectorLabel(String.valueOf(j)), nodes.get(j));
                    }
                }
            }
        } else {
            // TODO
            TIntArrayList nodes = new TIntArrayList(varCount);
            builder.addNodes(type, varCount, nodes);
            TIntArrayList variables = canonicalHc.variableEdges();
            for(int i=0; i < varCount; i++) {
                int varFrom = variables.get(i);
                int from = canonicalHc.targetOf(varFrom);
            }
        }
        return builder.build();
    }

    @Override
    public boolean isInitialState(HeapAutomatonState heapAutomatonState) {

        return true;
    }

    @Override
    public List<HeapConfiguration> getPossibleHeapRewritings(HeapConfiguration heapConfiguration) {

        return Collections.singletonList(heapConfiguration);
    }
}

class ReachabilityAutomatonState extends HeapAutomatonState {

    final HeapConfiguration kernel;

    ReachabilityAutomatonState(HeapConfiguration kernel) {

        this.kernel = kernel;
    }

    @Override
    public Set<String> toAtomicPropositions() {

        Set<String> result = new HashSet<>();
        TIntArrayList nodes = kernel.nodes();
        for(int i=0; i < nodes.size(); i++) {
            int u = nodes.get(i);
            TIntIterator iter = kernel.successorNodesOf(u).iterator();
            while(iter.hasNext()) {
                int v = iter.next();
                result.add("isReachable(" + u + "," + v + ")");
            }
        }
        return result;
    }

    public String toString() {

        StringBuilder result = new StringBuilder();
        TIntArrayList nodes = kernel.nodes();
        for(int i=0; i < nodes.size(); i++) {
            int u = nodes.get(i);
            TIntIterator iter = kernel.successorNodesOf(u).iterator();
            while(iter.hasNext()) {
                int v = iter.next();
                result.append("(");
                result.append(u);
                result.append(",");
                result.append(v);
                result.append(")");
            }
        }
        return result.toString();
    }

    @Override
    public boolean isError() {

        return false;
    }

    @Override
    public boolean equals(Object otherObject) {

        if(otherObject == this) {
            return true;
        }

        if(otherObject == null) {
            return false;
        }

        if(otherObject.getClass() != ReachabilityAutomatonState.class) {
            return false;
        }

        ReachabilityAutomatonState other = (ReachabilityAutomatonState) otherObject;
        return kernel.equals(other.kernel);
    }

    @Override
    public int hashCode() {

        return kernel.hashCode();
    }
}

class ReachabilityHelper {

    /**
     * The heap configuration in which the reachability between
     * nodes shall be computed.
     */
    private HeapConfiguration heapConfiguration;

    /**
     * Stores the set of reachable nodes for each node
     * in heapConfiguration.
     */
    private Map<Integer, TIntSet> reachableNodes;

    /**
     * True if and only if the reachableNodes have changed during
     * the last iteration of the fixpoint computation used to
     * determine all reachable nodes.
     */
    private boolean hasChanged;

    /**
     * @param heapConfiguration The heap configuration whose reachable nodes
     *                          shall be determined for each nodes.
     */
    ReachabilityHelper(HeapConfiguration heapConfiguration) {

        this.heapConfiguration = heapConfiguration;
        initReachableNodes();
        computeReachableNodes();
    }

    /**
     * Initializes the reachable nodes with the base case: The direct successors
     * of each node.
     */
    private void initReachableNodes() {

        int size = heapConfiguration.countNodes();
        reachableNodes = new HashMap<>(size);
        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            TIntArrayList successors = heapConfiguration.successorNodesOf(node);
            TIntSet reachable = new TIntHashSet(successors);
            reachableNodes.put(node, reachable);
        }
    }

    /**
     * Performs a fixpoint computation to determine all reachable nodes.
     */
    private void computeReachableNodes() {

        do {
            hasChanged = false;
            updateReachableNodes();
        } while(hasChanged);
    }

    /**
     * Performs a single step of the fixpoint computation to determine all reachable nodes.
     * That is, for each reachable node of any node, all its reachable nodes are also marked
     * as reachable. In case at least one additional node has been marked as reachable, this
     * method sets hasChanged to true.
     */
    private void updateReachableNodes() {

        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            TIntSet successors = reachableNodes.get(node);
            TIntSet update = new TIntHashSet();
            TIntIterator succIter = successors.iterator();
            while(succIter.hasNext()) {
                int succ = succIter.next();
                update.addAll(heapConfiguration.successorNodesOf(succ));
            }
            if(!successors.containsAll(update)) {
                hasChanged = true;
                successors.addAll(update);
            }
        }
    }

    /**
     * Checks whether the node 'to' is reachable from the node 'from'.
     * @param from The source node.
     * @param to The node that should be reached.
     * @return True if and only if node 'to' is reachable from node 'from'.
     */
    boolean isReachable(int from, int to) {

        return reachableNodes.containsKey(from)
                && reachableNodes.get(from).contains(to);
    }
}
