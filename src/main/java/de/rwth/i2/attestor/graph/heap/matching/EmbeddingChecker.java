package de.rwth.i2.attestor.graph.heap.matching;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2EmbeddingChecker;

/**
 * An {@link AbstractMatchingChecker} to compute an embedding of a pattern HeapConfiguration
 * in a target HeapConfiguration.
 *
 * @author Christoph
 */
public class EmbeddingChecker extends AbstractMatchingChecker {

    /**
     * Initializes an EmbeddingChecker.
     *
     * @param pattern The HeapConfiguration that should be embedded in the HeapConfiguration target.
     * @param target  The HeapConfiguration in which an embedding should be searched for.
     */
    public EmbeddingChecker(HeapConfiguration pattern, HeapConfiguration target) {

        super(pattern, target, new VF2EmbeddingChecker());
    }

}
