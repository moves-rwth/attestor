package de.rwth.i2.attestor.tasks.defaultTask;

import java.util.HashSet;
import java.util.Set;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.SingleElementUtil;

/**
 * The strategy to abstract program states based on a standard
 * hyperedge replacement grammars.
 *
 * @author Christoph
 */
public class DefaultCanonicalizationStrategy implements CanonicalizationStrategy {

    /**
     * The grammar that guides abstraction.
     */
	private Grammar grammar;

    /**
     * A flag that determines whether the grammar is backward confluent.
     * If this is the case, it suffices to consider a single sequence of inverse derivations.
     */
	private boolean isConfluent;

    /**
     * A flat that prevents abstraction of program states whose corresponding program location
     * has at most one successor.
     */
	private boolean ignoreUniqueSuccessorStatements;

    /**
     * Initializes the strategy.
     * @param grammar The grammar that guides abstraction.
     * @param isConfluent True if and only if the grammar is backward confluent.
     */
	public DefaultCanonicalizationStrategy(Grammar grammar, boolean isConfluent) {	
		this.grammar = grammar;
		this.isConfluent = isConfluent;
		this.ignoreUniqueSuccessorStatements = false;
	}

    /**
     * Sets a flag to prevent abstraction of program states with at most one successor.
     * @param enabled True if and only if program states with at most one successor are not abstracted.
     */
	public void setIgnoreUniqueSuccessorStatements(boolean enabled) {
		ignoreUniqueSuccessorStatements = enabled;
	}

    /**
     * Marks the underlying grammar as confluent.
     */
	public void setConfluent() {
		
		this.isConfluent = true;
	}
	
	
	@Override
	public Set<ProgramState> canonicalize(Semantics semantics, ProgramState conf) {
		
		DefaultState state = (DefaultState) conf;
		
		state = state.clone();
		
		if(ignoreUniqueSuccessorStatements && !semantics.permitsCanonicalization()) {
			
			return SingleElementUtil.createSet( state );
		}
		
		return performCanonization(state);
	}

    /**
     * Performs the actual grammar based abstraction of a given program state.
     * @param state The program state that should be abstracted.
     * @return The set of abstracted program states.
     */
	private Set<ProgramState> performCanonization(DefaultState state) {

		Set<ProgramState> result = new HashSet<>();
		
		boolean checkNext = true;
		
		for(Nonterminal nonterminal : grammar.getAllLeftHandSides() ) {
			
			if(!checkNext) { break; }
			
			for(HeapConfiguration pattern : grammar.getRightHandSidesFor(nonterminal) ) {
			
				if(!checkNext) { break; }
				
				AbstractMatchingChecker checker = state.getHeap().getEmbeddingsOf(pattern);

				while(checker.hasNext() && checkNext) {
					
					checkNext = !isConfluent; 
					
				    DefaultState abstracted  = state;
					if(checkNext) {
						abstracted = state.clone();
					}
					
					Matching embedding = checker.getNext();
					
					abstracted.getHeap().builder().replaceMatching( embedding , nonterminal).build();
					result.addAll( performCanonization( abstracted ) );
				}
				
			}
		}
		
		if(result.isEmpty()) {	
			
			result.add(state);
		}
		
		return result;
	}
	
	
}
