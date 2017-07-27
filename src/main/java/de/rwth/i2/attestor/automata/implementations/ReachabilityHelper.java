package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashMap;
import java.util.Map;

public class ReachabilityHelper {

    private HeapConfiguration heapConfiguration;
    private Map<Integer, TIntSet>  reachableNodes;
    private boolean hasChanged;

    public ReachabilityHelper(HeapConfiguration heapConfiguration) {

        this.heapConfiguration = heapConfiguration;
        initReachableNodes();

        computeReachableNodes();
    }

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

    private void computeReachableNodes() {

        do {
            hasChanged = false;
            updateReachableNodes();
        } while(hasChanged);
    }

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





    public boolean isReachable(int from, int to) {

        return reachableNodes.containsKey(from)
                && reachableNodes.get(from).contains(to);
    }
}
