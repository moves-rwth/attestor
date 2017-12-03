package de.rwth.i2.attestor.graph.heap.matching;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2PreconditionChecker;

public class PreconditionChecker extends AbstractMatchingChecker {

    /**
     * Initializes this Wrapper to search for isomorphisms between HeapConfiguration.
     *
     * @param pattern A HeapConfiguration that should be checked whether it is isomorphic to target.
     * @param target  A HeapConfiguration that should be checked whether it is isomorphic to pattern.
     */
    public PreconditionChecker(HeapConfiguration pattern, HeapConfiguration target) {

        super(pattern, target, new VF2PreconditionChecker());
    }
}
