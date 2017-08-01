package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.FactorySettings;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;


public class ReachabilityAutomaton extends HeapAutomaton {

    @Override
    protected AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        assert(ntAssignment.size() == heapConfiguration.countNonterminalEdges());

        HeapConfiguration canonicalHc = computeCanonicalHc(ntAssignment, heapConfiguration);
        ReachabilityHelper helper = new ReachabilityHelper(canonicalHc);

        return new ReachabilityState(
            computeReachableExternals(canonicalHc, helper),
            computeAtomicPropositions(canonicalHc, helper)
        );
    }

    private HeapConfiguration computeCanonicalHc(List<AutomatonState> ntAssignment,
                                                 HeapConfiguration heapConfiguration) {

        heapConfiguration = heapConfiguration.clone();
        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        for(int i=0; i < ntAssignment.size(); i++) {
            AutomatonState state = ntAssignment.get(i);
            assert(state instanceof ReachabilityState);
            ReachabilityState rState = (ReachabilityState) state;
            int edge = ntEdges.get(i);
            heapConfiguration.builder().replaceNonterminalEdge(edge, rState.kernel);
        }
        return heapConfiguration.builder().build();
    }

    private List<TIntSet> computeReachableExternals(HeapConfiguration canonicalHc, ReachabilityHelper helper) {

        List<TIntSet> reachabilityRelation = new ArrayList<>();
        int size = canonicalHc.countExternalNodes();
        for(int i=0; i < size; i++) {
            int ext1 = canonicalHc.externalNodeAt(i);
            TIntSet set = new TIntHashSet(size);
            for(int j=0; j < size; j++) {
                int ext2 = canonicalHc.externalNodeAt(j);
                if(helper.isReachable(ext1, ext2)) {
                    set.add(j);
                }
            }
            reachabilityRelation.add(set);
        }
        return reachabilityRelation;
    }

    private Set<String> computeAtomicPropositions(HeapConfiguration canonicalHc, ReachabilityHelper helper) {

        Set<String> result = new HashSet<>();
        TIntArrayList variables = canonicalHc.variableEdges();
        for(int i=0; i < variables.size(); i++) {
            int from = variables.get(i);
            int fromNode = canonicalHc.targetOf(from);
            for(int j=0; j < variables.size(); j++) {
                int to = variables.get(j);
                int toNode = canonicalHc.targetOf(to);
                if(helper.isReachable(fromNode, toNode)) {
                    String ap = "(" + canonicalHc.nameOf(from) + ", " + canonicalHc.nameOf(to) + ")";
                    result.add(ap);
                }
            }
        }
        return result;
    }
}


class ReachabilityState implements AutomatonState {

    final HeapConfiguration kernel;
    private final Set<String> atomicPropositions;

    ReachabilityState(List<TIntSet> reachabilityRelation, Set<String> atomicPropositions) {

        kernel = computeKernel(reachabilityRelation);
        this.atomicPropositions = atomicPropositions;
    }

    private HeapConfiguration computeKernel(List<TIntSet> reachabilityRelation) {

        FactorySettings factory = Settings.getInstance().factory();

        int rank = reachabilityRelation.size();
        Type type = factory.getType("kernelNode");
        HeapConfiguration kernel = factory.createEmptyHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList(rank);
        kernel.builder().addNodes(type, rank, nodes);

        for(int i=0; i < rank; i++) {
            kernel.builder().setExternal(i);
            TIntSet targets = reachabilityRelation.get(i);
            TIntIterator iter = targets.iterator();
            while(iter.hasNext()) {
                int j = iter.next();
                kernel.builder().addSelector(i, factory.getSelectorLabel(String.valueOf(j)), j);
            }
        }

        return kernel.builder().build();
    }

    @Override
    public boolean isFinal() {

        return atomicPropositions.isEmpty();
    }

    @Override
    public Set<String> getAtomicPropositions() {

        return atomicPropositions;
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
