package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Auxiliary class to compute for each nodes the set of reachable nodes using directed selector edges.
 * This information can be accessed via {@link #isReachable(int, int)}.
 *
 * @author Christoph
 */
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
    private Map<Integer, TIntSet>  reachableNodes;

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
