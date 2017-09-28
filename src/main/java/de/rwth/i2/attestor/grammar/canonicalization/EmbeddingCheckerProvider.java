package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.semantics.TerminalStatement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnValueStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnVoidStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

/**
 * This class is responsible to select the correct embeddingChecker
 * for given settings and semantics
 * 
 * @author Hannah
 *
 */
public class EmbeddingCheckerProvider {
	
	private int minDereferenceDepth;
	private int aggressiveAbstractionThreshold;
	private boolean aggressiveReturnAbstraction;

	/**
	 * Constructs an EmbeddingCheckerProvider with the given settings
	 * @param minDereferenceDepth the distance which has to be ensured between an embedding and
	 * the next node referenced by a variable
	 * @param aggressiveAbstractionThreshold any graphs larger than this threshold will be abstracted
	 * without considering the minDereferenceDepth
	 * @param aggressiveReturnAbstraction if enabled, terminal states will be abstracted without considering
	 * the minDereferenceDepth
	 */
	public EmbeddingCheckerProvider( int minDereferenceDepth,
			int aggressiveAbstractionThreshold, boolean aggressiveReturnAbstraction) {
		this.minDereferenceDepth = minDereferenceDepth;
		this.aggressiveAbstractionThreshold = aggressiveAbstractionThreshold;
		this.aggressiveReturnAbstraction = aggressiveReturnAbstraction;
	}

	/**
	 * For the given target and pattern, gets the correct EmbeddingCheckerType for the stored 
	 * settings and the given semantics
	 * @param graph the target graph
	 * @param pattern the graph which will be embedded
	 * @param semantics the current semantics
	 * @return the correct EmbeddingChecker
	 */
	public AbstractMatchingChecker getEmbeddingChecker(HeapConfiguration graph, HeapConfiguration pattern,
			Semantics semantics) {

		Class sClass = semantics.getClass();

		if( graph.countNodes() > aggressiveAbstractionThreshold || (aggressiveReturnAbstraction
				&& (
						sClass == ReturnValueStmt.class
				     || sClass == ReturnVoidStmt.class
					 || sClass == TerminalStatement.class
				)
		)) {
			return graph.getEmbeddingsOf(pattern, 0);
		}

		return graph.getEmbeddingsOf(pattern, minDereferenceDepth);
	}

}
