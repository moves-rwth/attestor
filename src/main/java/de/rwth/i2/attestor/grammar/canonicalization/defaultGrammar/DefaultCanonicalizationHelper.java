package de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar;

import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class DefaultCanonicalizationHelper implements CanonicalizationHelper {

	public EmbeddingCheckerProvider provider;


	public DefaultCanonicalizationHelper( EmbeddingCheckerProvider provider ) {
		super();
		this.provider = provider;
	}

	/* (non-Javadoc)
	 * @see de.rwth.i2.attestor.grammar.canonicalization.MatchingHandler#tryReplaceMatching(de.rwth.i2.attestor.stateSpaceGeneration.ProgramState, de.rwth.i2.attestor.graph.heap.HeapConfiguration, de.rwth.i2.attestor.graph.Nonterminal, de.rwth.i2.attestor.stateSpaceGeneration.Semantics, boolean)
	 */
	@Override
	public ProgramState tryReplaceMatching( ProgramState toAbstract, 
			HeapConfiguration rhs, Nonterminal lhs,
			Semantics semantics) {


		AbstractMatchingChecker checker = 
				provider.getEmbeddingChecker(toAbstract.getHeap(), rhs, semantics);

		if( checker.hasMatching() ) {

			Matching embedding = checker.getMatching();
			return replaceEmbeddingBy( toAbstract, embedding, lhs );
		}
		return null;
	}

	/**
	 * replaces the embedding in  abstracted by the given nonterminal
	 * 
	 * @param stateToAbstract the outer graph.
	 * @param embedding the embedding of the inner graph in the outer graph
	 * @param nonterminal the nonterminal to replace the embedding
	 */
	private ProgramState replaceEmbeddingBy( ProgramState stateToAbstract, Matching embedding, Nonterminal nonterminal) {
		HeapConfiguration toAbstract = stateToAbstract.clone().getHeap();
		HeapConfiguration abstracted = toAbstract.clone().builder().replaceMatching( embedding, nonterminal ).build();
		return stateToAbstract.shallowCopyWithUpdateHeap( abstracted );
	}

	@Override
	public ProgramState prepareHeapForCanonicalization(ProgramState toAbstract) {
		return toAbstract;
	}


}