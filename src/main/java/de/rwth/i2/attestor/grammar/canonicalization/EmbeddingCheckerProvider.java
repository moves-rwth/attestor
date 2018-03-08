package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;

/**
 * This class is responsible to select the correct embeddingChecker
 * for given communication and semantics
 *
 * @author Hannah
 */
public class EmbeddingCheckerProvider {

    private final int minDereferenceDepth;
    private final boolean aggressiveNullAbstractionEnabled;
    private final boolean aggressiveCompositeMarkingAbstraction;

    /**
     * Constructs an EmbeddingCheckerProvider with the given communication
     *
     * @param minDereferenceDepth the distance which has to be ensured between an embedding and
     *                            the next node referenced by a variable
     */
    public EmbeddingCheckerProvider(int minDereferenceDepth, boolean aggressiveNullAbstractionEnabled,
                                    boolean aggressiveCompositeMarkingAbstraction) {

        this.minDereferenceDepth = minDereferenceDepth;
        this.aggressiveNullAbstractionEnabled = aggressiveNullAbstractionEnabled;
        this.aggressiveCompositeMarkingAbstraction = aggressiveCompositeMarkingAbstraction;
    }

    /**
     * For the given target and pattern, gets the correct EmbeddingCheckerType for the stored
     * communication and the given semantics
     *
     * @param graph   the target graph
     * @param pattern the graph which will be embedded
     * @return the correct EmbeddingChecker
     */
    public AbstractMatchingChecker getEmbeddingChecker(HeapConfiguration graph, HeapConfiguration pattern) {

        return graph.getEmbeddingsOf(pattern, minDereferenceDepth,
                aggressiveNullAbstractionEnabled, aggressiveCompositeMarkingAbstraction);
    }

}
