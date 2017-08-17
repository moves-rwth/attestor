package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.*;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.heap.matching.EmbeddingChecker;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnValueStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnVoidStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;
import gnu.trove.iterator.TIntIterator;

public class IndexedCanonicalizationStrategy implements CanonicalizationStrategy {
	private static final Logger logger = LogManager.getLogger( "IndexedCanonicalizationStrategy" );

	/**
	 * The grammar that guides abstraction.
	 */
	private Grammar grammar;

	/**
	 * A flag that determines whether the grammar is backward confluent.
	 * If this is the case, it suffices to consider a single sequence of inverse derivations.
	 */
	private final boolean isConfluent;

	/**
	 * A flag that prevents abstraction of program states whose corresponding program location
	 * has at most one successor.
	 */
	private boolean ignoreUniqueSuccessorStatements;


	private final IndexCanonizationStrategy indexCanonizationStrategy;
	private final int aggressiveAbstractionThreshold;

	private final boolean aggressiveReturnAbstraction;


	/**
	 * Initializes the strategy.
	 * @param grammar The grammar that guides abstraction.
	 * @param isConfluent True if and only if the grammar is backward confluent.
	 */
	public IndexedCanonicalizationStrategy(Grammar grammar, boolean isConfluent,
										   int aggressiveAbstractionThreshold,
										   boolean aggressiveReturnAbstraction) {
		this.grammar = grammar;
		this.isConfluent = isConfluent;

		this.indexCanonizationStrategy = new AVLIndexCanonizationStrategy();
		this.aggressiveAbstractionThreshold = aggressiveAbstractionThreshold;
		this.aggressiveReturnAbstraction = aggressiveReturnAbstraction;
	}

	/**
	 * Performs the actual grammar based abstraction of a given program state.
	 * @param semantics The program statement executed prior to canonicalization.
	 * @param state The program state that should be abstracted.
	 * @return The set of abstracted program states.
	 */
	@Override
	public Set<ProgramState> canonicalize(Semantics semantics, ProgramState state) {

		

		if( state.getHeap().countNodes() > aggressiveAbstractionThreshold) {

			logger.trace( "Using aggressive canonization" );
			return performCanonicalization( state, true );
		}else if(aggressiveReturnAbstraction
				&&
				(semantics instanceof ReturnValueStmt || semantics instanceof ReturnVoidStmt) ){
			return performCanonicalization( state, true );
		}
		
		return performCanonicalization( state, false);
	}

	

	private Set<ProgramState> performCanonicalization(ProgramState state, boolean strongCanonicalization) {

		Set<ProgramState> result = new HashSet<>();

		boolean success = false;

		for( Nonterminal nt : grammar.getAllLeftHandSides() ) {

			IndexedNonterminal nonterminal = (IndexedNonterminal) nt;

			if( success && isConfluent ) { break; }

			for(HeapConfiguration pattern : grammar.getRightHandSidesFor(nonterminal) ) {

				if( success && isConfluent ) { break; }

				indexCanonizationStrategy.canonizeStack( state.getHeap() );
				
				AbstractMatchingChecker checker;
				if( strongCanonicalization ){
					checker = new EmbeddingChecker(pattern, state.getHeap() );
				}else{
					checker = state.getHeap().getEmbeddingsOf(pattern);
				}
				
				
				while(checker.hasNext() && ( !success || !isConfluent ) ) {

					ProgramState abstracted  = state;
				
					Matching embedding = checker.getNext();
					resetInstantiation(nonterminal);
					success = checkIndexMatching(pattern, abstracted, embedding);
					if( success ){
						replaceEmbeddingBy(abstracted, embedding, nonterminal);
						result.addAll( performCanonicalization( abstracted, strongCanonicalization ) );
					}
				}

			}
		}

		if(result.isEmpty()) {	

			result.add(state);
		}

		return result;
	}
	
	private void resetInstantiation(IndexedNonterminal nonterminal) {

		IndexSymbol lastSymb = nonterminal.getIndex().getLastStackSymbol();
		if( lastSymb instanceof IndexVariable){
			( (IndexVariable) lastSymb ).resetInstantiation();
		}
	}

	private void replaceEmbeddingBy(ProgramState abstracted, Matching embedding, IndexedNonterminal nonterminal) {
		abstracted = abstracted.clone();
		
		HeapConfigurationBuilder builder = abstracted.getHeap().builder();
		builder.replaceMatching( embedding , nonterminal );

		TIntIterator iter = abstracted.getHeap().nonterminalEdges().iterator();
		while(iter.hasNext()) {
			int edge = iter.next();
			IndexedNonterminal label = (IndexedNonterminal) abstracted.getHeap().labelOf(edge);
			builder.replaceNonterminal(edge, label.getWithInstantiation() );
		}
		builder.build();
		indexCanonizationStrategy.canonizeStack( abstracted.getHeap() );
	}

	private boolean checkIndexMatching(HeapConfiguration pattern, ProgramState abstracted, Matching embedding) {
		boolean indexMatch = true;
		
		TIntIterator ntEdgeIter = pattern.nonterminalEdges().iterator();
		while(ntEdgeIter.hasNext()) {

			int edge = ntEdgeIter.next();

			IndexedNonterminal patternNt = (IndexedNonterminal) pattern.labelOf(edge);
			IndexedNonterminal targetNt =  (IndexedNonterminal) abstracted.getHeap().labelOf( embedding.match( edge ) );
			indexMatch = targetNt.getIndex().matchStack(patternNt.getIndex());
			if( ! indexMatch ){
				break;
			}	
		}
		return indexMatch;
	}

}
