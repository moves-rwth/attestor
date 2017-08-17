

package de.rwth.i2.attestor.grammar.canonicalization;

import java.util.HashSet;
import java.util.Set;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.SingleElementUtil;

public class GeneralCanonicalizationStrategy implements CanonicalizationStrategy {

	private Grammar grammar; 
	private CanonicalizationHelper canonicalizationHelper;

	public GeneralCanonicalizationStrategy( Grammar grammar, 
											CanonicalizationHelper matchingHandler ) {

		this.grammar = grammar;
		this.canonicalizationHelper = matchingHandler;
	}

	@Override
	public Set<ProgramState> canonicalize(Semantics semantics, ProgramState state ) {

		if( !semantics.permitsCanonicalization() ) { 

			return SingleElementUtil.createSet( state );
		}

		return performCanonicalization( semantics, state );
	}

	private Set<ProgramState> performCanonicalization(Semantics semantics, ProgramState state) {

		state = canonicalizationHelper.prepareHeapForCanonicalization( state );
		
		Set<ProgramState> result = new HashSet<>();

		boolean success = false;

		for( Nonterminal lhs : grammar.getAllLeftHandSides() ){

			if( success ) { break; }

			for( HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs) ){

				if( success ) { break; }

				ProgramState abstractedState = 
						canonicalizationHelper.tryReplaceMatching(state, rhs, lhs, semantics );
				if( abstractedState != null ) {
					success = true;	
					result.addAll( performCanonicalization( semantics, abstractedState ) );
				}
			}			
		}

		if(result.isEmpty()) {	

			result.add(state);
		}

		return result;
	}





}
