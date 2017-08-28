

package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.util.SingleElementUtil;

import java.util.HashSet;
import java.util.Set;

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

		abstractionFound:
        for( Nonterminal lhs : grammar.getAllLeftHandSides() ){
			for( HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs) ){
				ProgramState abstractedState =
						canonicalizationHelper.tryReplaceMatching(state, rhs, lhs, semantics );
				if( abstractedState != null ) {
					result.addAll( performCanonicalization( semantics, abstractedState ) );
					break abstractionFound;
				}
			}			
		}

		if(result.isEmpty()) {
			result.add(state);
		}

		return result;
	}





}
