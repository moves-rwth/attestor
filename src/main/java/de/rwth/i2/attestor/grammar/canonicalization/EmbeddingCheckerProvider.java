package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.morphism.MorphismOptions;

/**
 * This class is responsible to select the correct embeddingChecker
 * for given communication and semantics
 *
 * @author Hannah
 */
public class EmbeddingCheckerProvider {

    private final MorphismOptions morphismOptions;

    /**
     * Constructs an EmbeddingCheckerProvider with the given communication
     *
     * @param morphismOptions Options guiding how embeddings are computed.
     */
    public EmbeddingCheckerProvider(MorphismOptions morphismOptions) {

        this.morphismOptions = morphismOptions;
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

        return graph.getEmbeddingsOf(pattern, morphismOptions);
    }

}
