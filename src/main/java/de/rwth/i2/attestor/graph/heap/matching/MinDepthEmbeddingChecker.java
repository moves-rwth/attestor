package de.rwth.i2.attestor.graph.heap.matching;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2MinDepthEmbeddingChecker;

/**
 * 
 * An {@link AbstractMatchingChecker} that computes an embedding of a pattern HeapConfiguration
 * in a target HeapConfiguration with one additional constraint:
 * Each variable hyperedge must have at least a predefined distance to all elements in an embedding.
 * This is a relaxed version of embeddings between HeapConfigurations that allows for less aggressive abstractions.
 *
 * @see EmbeddingChecker
 * 
 * @author Christoph
 *
 */
public class MinDepthEmbeddingChecker extends AbstractMatchingChecker{

	/**
	 * Initializes an EmbeddingChecker with a minimal distance between variables and found embeddings.
	 * @param pattern The HeapConfiguration that should be embedded in the pattern HeapConfiguration.
	 * @param target The HeapConfiguration in which embeddings should be searched for.
	 * @param depth The minimal distance between an element of a found embedding and a variable in the pattern
     *              HeapConfiguration.
	 */
	public MinDepthEmbeddingChecker(HeapConfiguration pattern, HeapConfiguration target, int depth) {
		super(pattern, target, new VF2MinDepthEmbeddingChecker(depth));
	}
}
