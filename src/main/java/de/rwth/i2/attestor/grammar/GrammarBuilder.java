package de.rwth.i2.attestor.grammar;

import java.util.*;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * Used to construct a grammar.
 * By default, the grammar is not marked as backwards confluent.
 * Use setConfluent() to change this.
 * 
 * @author Hannah
 *
 */
public class GrammarBuilder {
	
	Map<Nonterminal, Set<HeapConfiguration>> rules = new HashMap<>();
	
	public Grammar build() {
		return new Grammar( rules );
	}
	
	public GrammarBuilder addRule( Nonterminal lhs, HeapConfiguration rhs) {
		if( ! rules.containsKey(lhs) ){
			rules.put(lhs, new HashSet<>() );
		}
		
		rules.get(lhs).add(rhs);
		
		return this;
	}

	public GrammarBuilder addRules(Nonterminal lhs, Collection<HeapConfiguration> rightHandSides ) {
		if( ! rules.containsKey(lhs) ){
			rules.put(lhs, new HashSet<>() );
		}
		rules.get(lhs).addAll( rightHandSides );
		return this;
	}

	public GrammarBuilder addRules(Map<Nonterminal, Collection<HeapConfiguration>> newRules ) {
		for (Map.Entry<Nonterminal, Collection<HeapConfiguration>> ruleEntry : newRules.entrySet() ) {
			this.addRules( ruleEntry.getKey(), ruleEntry.getValue() );
		}
		return this;
	}
	
	
}
