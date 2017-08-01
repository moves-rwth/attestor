package de.rwth.i2.attestor.automata.implementations.reachability;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * Specialized transition relation to check reachability between external nodes.
 *
 * @author Christoph
 */
public class ExternalReachabilityTransitionRelation extends ReachabilityTransitionRelation {

    /**
     * Position of the source external node.
     */
    private int fromExternalNode;

    /**
     * Position of the target external node.
     */
    private int toExternalNode;

    /**
     * @param fromExternalNode Position of the source external node in the sequence of external nodes.
     * @param toExternalNode Position of the target external node in the sequence of external nodes.
     */
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
