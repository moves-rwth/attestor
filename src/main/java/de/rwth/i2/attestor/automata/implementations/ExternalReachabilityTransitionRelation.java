package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class ExternalReachabilityTransitionRelation extends ReachabilityTransitionRelation {

    private int fromExternalNode;
    private int toExternalNode;

    ExternalReachabilityTransitionRelation(int fromExternalNode, int toExternalNode) {

        super();
        this.fromExternalNode = fromExternalNode;
        this.toExternalNode = toExternalNode;
    }

    @Override
    protected boolean isFinalState(HeapConfiguration canonicalHc, ReachabilityHelper helper) {

        return fromExternalNode < canonicalHc.countExternalNodes()
                && toExternalNode < canonicalHc.countExternalNodes()
                && helper.isReachable(
                        canonicalHc.externalNodeAt(fromExternalNode),
                        canonicalHc.externalNodeAt(toExternalNode)
                    );
    }
}
