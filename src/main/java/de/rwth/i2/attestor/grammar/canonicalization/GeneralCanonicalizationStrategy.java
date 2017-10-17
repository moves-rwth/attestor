

package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class GeneralCanonicalizationStrategy implements CanonicalizationStrategy {

	private Grammar grammar; 
	private CanonicalizationHelper canonicalizationHelper;

	public GeneralCanonicalizationStrategy( Grammar grammar, 
											CanonicalizationHelper canonicalizationHelper ) {

		this.grammar = grammar;
		this.canonicalizationHelper = canonicalizationHelper;
	}

	@Override
	public ProgramState canonicalize(Semantics semantics, ProgramState state ) {

		return performCanonicalization( semantics, state );
	}

	private ProgramState performCanonicalization(Semantics semantics, ProgramState state) {

		state = canonicalizationHelper.prepareHeapForCanonicalization( state );

		for( Nonterminal lhs : grammar.getAllLeftHandSides() ){
			for( HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs) ){
				ProgramState abstractedState =
						canonicalizationHelper.tryReplaceMatching(state, rhs, lhs, semantics );
				if( abstractedState != null ) {
					return performCanonicalization( semantics, abstractedState );
				}
			}
		}

		return state;
	}





}
