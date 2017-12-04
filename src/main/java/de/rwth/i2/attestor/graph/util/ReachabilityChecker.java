package de.rwth.i2.attestor.graph.util;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class ReachabilityChecker {

    private HeapConfiguration heapConfiguration;
    private TIntCollection sourceNodes;
    private TIntSet reachableNodes;
    private TIntLinkedList queue;

    public ReachabilityChecker(HeapConfiguration heapConfiguration, TIntCollection sourceNodes) {

        this.heapConfiguration = heapConfiguration;
        this.sourceNodes = sourceNodes;
        this.reachableNodes = new TIntHashSet(sourceNodes);
        this.queue = new TIntLinkedList();
        queue.addAll(sourceNodes);

        computeReachableNodes();
    }

    private void computeReachableNodes() {

        while (!queue.isEmpty()) {
            int node = queue.removeAt(0);
            updateNodesReachableViaSelector(node);
            updateNodesReachableViaNonterminal(node);
        }
    }

    private void updateNodesReachableViaSelector(int node) {

        TIntIterator iterator = heapConfiguration.successorNodesOf(node).iterator();
        while (iterator.hasNext()) {
            int successorNode = iterator.next();
            if (reachableNodes.add(successorNode)) {
                queue.add(successorNode);
            }
        }
    }

    private void updateNodesReachableViaNonterminal(int node) {

        TIntIterator iterator = heapConfiguration.attachedNonterminalEdgesOf(node).iterator();
        while (iterator.hasNext()) {
            int ntEdge = iterator.next();
            Nonterminal label = heapConfiguration.labelOf(ntEdge);
            TIntArrayList attachedNodes = heapConfiguration.attachedNodesOf(ntEdge);
            int tentacle = attachedNodes.indexOf(node);
            if (!label.isReductionTentacle(tentacle)) {
                TIntIterator attachedNodeIterator = attachedNodes.iterator();
                while (attachedNodeIterator.hasNext()) {
                    int successorNode = attachedNodeIterator.next();
                    if (reachableNodes.add(successorNode)) {
                        queue.add(successorNode);
                    }
                }
            }
        }
    }

    public TIntSet getReachableNodes() {

        return reachableNodes;
    }

    public TIntSet getUnreachableNodes() {

        TIntSet result = new TIntHashSet(heapConfiguration.nodes());
        result.removeAll(reachableNodes);
        return result;
    }
}
