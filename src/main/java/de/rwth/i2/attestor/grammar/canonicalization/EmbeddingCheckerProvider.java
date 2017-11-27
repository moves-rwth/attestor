package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;

/**
 * This class is responsible to select the correct embeddingChecker
 * for given settings and semantics
 * 
 * @author Hannah
 *
 */
public class EmbeddingCheckerProvider {
	
	private final int minDereferenceDepth;

	/**
	 * Constructs an EmbeddingCheckerProvider with the given settings
	 * @param minDereferenceDepth the distance which has to be ensured between an embedding and
	 * the next node referenced by a variable
	 */
	public EmbeddingCheckerProvider( int minDereferenceDepth ) {
		this.minDereferenceDepth = minDereferenceDepth;
	}

	/**
	 * For the given target and pattern, gets the correct EmbeddingCheckerType for the stored 
	 * settings and the given semantics
	 * @param graph the target graph
	 * @param pattern the graph which will be embedded
	 * @return the correct EmbeddingChecker
	 */
	public AbstractMatchingChecker getEmbeddingChecker(HeapConfiguration graph, HeapConfiguration pattern) {

		return graph.getEmbeddingsOf(pattern, minDereferenceDepth);
	}

}
