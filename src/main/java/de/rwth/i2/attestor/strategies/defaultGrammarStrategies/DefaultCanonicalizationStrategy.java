package de.rwth.i2.attestor.strategies.defaultGrammarStrategies;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.heap.matching.EmbeddingChecker;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnValueStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnVoidStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.util.SingleElementUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * The strategy to abstract program states based on a standard
 * hyperedge replacement grammars.
 *
 * @author Christoph
 */
public class DefaultCanonicalizationStrategy implements CanonicalizationStrategy {
	private static final Logger logger = LogManager.getLogger( "DefaultCanonicalizationStrategy" );

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

    private final int aggressiveAbstractionThreshold;

    private final boolean aggressiveReturnAbstraction;

    private final int minAbstractionDistance;

	/**
	 * Initializes the strategy.
	 * @param grammar The grammar that guides abstraction.
	 * @param isConfluent True if and only if the grammar is backward confluent.
	 */
	public DefaultCanonicalizationStrategy(Grammar grammar, boolean isConfluent,
                                           int aggressiveAbstractionThreshold,
                                           boolean aggressiveReturnAbstraction,
										   int minAbstractionDistance) {

		this.grammar = grammar;
		this.isConfluent = isConfluent;
		this.ignoreUniqueSuccessorStatements = true;
		this.aggressiveAbstractionThreshold = aggressiveAbstractionThreshold;
		this.aggressiveReturnAbstraction = aggressiveReturnAbstraction;
		this.minAbstractionDistance = minAbstractionDistance;
	}

	/**
	 * Sets a flag to prevent abstraction of program states with at most one successor.
	 * @param enabled True if and only if program states with at most one successor are not abstracted.
	 */
	public void setIgnoreUniqueSuccessorStatements(boolean enabled) {
		ignoreUniqueSuccessorStatements = enabled;
	}

	/**
	 * Performs canonicalization with respect to the given statement to a program state.
	 * @param semantics The statement executed prior to canonicalization.
	 * @param state The program state that should be abstracted.
	 * @return The set of abstracted program states.
	 */
	@Override
	public Set<ProgramState> canonicalize(Semantics semantics, ProgramState state) {

		DefaultProgramState conf = (DefaultProgramState) state.clone();

		if( ignoreUniqueSuccessorStatements && !semantics.permitsCanonicalization() ) {

			return SingleElementUtil.createSet( conf );
		}
		if( conf.getHeap().countNodes() > aggressiveAbstractionThreshold ){
			logger.trace( "Using aggressive canonization" );
			return performCanonicalization( conf, true );
		}else if( aggressiveReturnAbstraction
				&& 
				( semantics instanceof ReturnValueStmt || semantics instanceof ReturnVoidStmt ) ){
			return performCanonicalization( conf, true );
		}

		return performCanonicalization(conf, false);
	}

	/**
	 * Performs the actual grammar based abstraction of a given program state.
	 * @param state The program state that should be abstracted.
	 * @param strongCanonicalization if true, the abstraction will ignore the minDereferenceDepthOption
	 * @return The set of abstracted program states.
	 */
	private Set<ProgramState> performCanonicalization(DefaultProgramState state, boolean strongCanonicalization) {

		Set<ProgramState> result = new HashSet<>();
		boolean checkNext = true;

		for(Nonterminal nonterminal : grammar.getAllLeftHandSides() ) {

			if(!checkNext) { break; }

			for(HeapConfiguration pattern : grammar.getRightHandSidesFor(nonterminal) ) {

				if(!checkNext) { break; }

				AbstractMatchingChecker checker;
				if( strongCanonicalization ){
					checker = new EmbeddingChecker(pattern, state.getHeap() );
				}else{
					checker = state.getHeap().getEmbeddingsOf(pattern, minAbstractionDistance);
				}

				while(checker.hasNext() && checkNext) {

					checkNext = !isConfluent; 

					DefaultProgramState abstracted  = state;
					if(checkNext) {
						abstracted = state.clone();
					}

					Matching embedding = checker.getNext();

					replaceEmbeddingBy(abstracted, embedding, nonterminal);
					result.addAll( performCanonicalization( abstracted, strongCanonicalization ) );
				}

			}
		}

		if(result.isEmpty()) {	

			result.add(state);
		}

		return result;
	}

	private void replaceEmbeddingBy(DefaultProgramState abstracted, Matching embedding, Nonterminal nonterminal) {

		abstracted.getHeap().builder().replaceMatching( embedding , nonterminal).build();
	}

	
}
