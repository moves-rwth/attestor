package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.heap.matching.EmbeddingChecker;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.AVLStackCanonizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackCanonizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackVariable;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnValueStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnVoidStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import gnu.trove.iterator.TIntIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

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

	private final StackCanonizationStrategy stackCanonizationStrategy;

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
		this.stackCanonizationStrategy = new AVLStackCanonizationStrategy();
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

		IndexedState conf = ((IndexedState) state).clone();

		if( conf.getHeap().countNodes() > aggressiveAbstractionThreshold) {

			logger.trace( "Using aggressive canonization" );
			return performCanonicalization( conf, true );
		}else if(aggressiveReturnAbstraction
				&&
				(semantics instanceof ReturnValueStmt || semantics instanceof ReturnVoidStmt) ){
			return performCanonicalization( conf, true );
		}
		
		return performCanonicalization(conf, false);
	}

	

	private Set<ProgramState> performCanonicalization(IndexedState state, boolean strongCanonicalization) {

		Set<ProgramState> result = new HashSet<>();

		boolean checkNext = true;

		for( Nonterminal nt : grammar.getAllLeftHandSides() ) {

			IndexedNonterminal nonterminal = (IndexedNonterminal) nt;

			if(!checkNext) { break; }

			for(HeapConfiguration pattern : grammar.getRightHandSidesFor(nonterminal) ) {

				if( !checkNext ) { break; }

				stackCanonizationStrategy.canonizeStack( state.getHeap() );
				
				AbstractMatchingChecker checker;
				if( strongCanonicalization ){
					checker = new EmbeddingChecker(pattern, state.getHeap() );
				}else{
					checker = state.getHeap().getEmbeddingsOf(pattern);
				}
				
				while(checker.hasNext() && checkNext) {

					checkNext = !isConfluent; 

					IndexedState abstracted  = state;
					if( checkNext ) {
						abstracted = state.clone();
					}

					Matching embedding = checker.getNext();
					resetInstantiation(nonterminal);
					boolean indexMatch = checkIndexMatching(pattern, abstracted, embedding);
					if( indexMatch ){

						replaceEmbeddingBy(abstracted, embedding, nonterminal);
						result.addAll( performCanonicalization( abstracted, strongCanonicalization ) );
					}else{
						checkNext = true;
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
		StackSymbol lastSymb = nonterminal.getStack().getLastStackSymbol();
		if( lastSymb instanceof StackVariable ){
			( (StackVariable) lastSymb ).resetInstantiation();
		}
	}

	private void replaceEmbeddingBy(IndexedState abstracted, Matching embedding, IndexedNonterminal nonterminal) {
		HeapConfigurationBuilder builder = abstracted.getHeap().builder();
		builder.replaceMatching( embedding , nonterminal );

		TIntIterator iter = abstracted.getHeap().nonterminalEdges().iterator();
		while(iter.hasNext()) {
			int edge = iter.next();
			IndexedNonterminal label = (IndexedNonterminal) abstracted.getHeap().labelOf(edge);
			builder.replaceNonterminal(edge, label.getWithInstantiation() );
		}
		builder.build();
		stackCanonizationStrategy.canonizeStack( abstracted.getHeap() );
	}

	private boolean checkIndexMatching(HeapConfiguration pattern, IndexedState abstracted, Matching embedding) {
		boolean indexMatch = true;
		
		TIntIterator ntEdgeIter = pattern.nonterminalEdges().iterator();
		while(ntEdgeIter.hasNext()) {

			int edge = ntEdgeIter.next();

			IndexedNonterminal patternNt = (IndexedNonterminal) pattern.labelOf(edge);
			IndexedNonterminal targetNt =  (IndexedNonterminal) abstracted.getHeap().labelOf( embedding.match( edge ) );
			indexMatch = targetNt.getStack().matchStack(patternNt.getStack());
			if( ! indexMatch ){
				break;
			}	
		}
		return indexMatch;
	}

}
