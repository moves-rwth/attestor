package de.rwth.i2.attestor.grammar;

import java.util.*;

import de.rwth.i2.attestor.grammar.materialization.GrammarBuilder;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class Grammar {
	
	public static GrammarBuilder builder(){
		return new GrammarBuilder();
	}

	Map<Nonterminal, Set<HeapConfiguration>> rules;
	
	public Grammar(Map<Nonterminal, Set<HeapConfiguration>> rules) {
		this.rules = rules;
	}

	/**
	 * 
	 * @param nonterminal
	 * @return an unmodifiable view of the rules' set
	 */
	public Set<HeapConfiguration> getRightHandSidesFor( Nonterminal nonterminal ) {
		if( rules.containsKey(nonterminal) ){
			return Collections.unmodifiableSet( rules.get(nonterminal) );
		}else{
			return new HashSet<>();
		}
	}


	/**
	 * 
	 * @return an unmodifiable view of the set of left hand sides
	 */
	public Set<Nonterminal> getAllLeftHandSides() {
		return Collections.unmodifiableSet( rules.keySet() );
	}


}
