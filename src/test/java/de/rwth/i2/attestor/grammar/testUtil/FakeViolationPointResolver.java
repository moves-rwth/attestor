package de.rwth.i2.attestor.grammar.testUtil;

import java.util.*;

import de.rwth.i2.attestor.grammar.materialization.ViolationPointResolver;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class FakeViolationPointResolver extends ViolationPointResolver {

	Collection<Nonterminal> nonterminalsInResultingKeySet;
	Collection<HeapConfiguration> heapsForResult;
	
	public FakeViolationPointResolver() {
		super(null);
	}
	
	@Override
	public Map<Nonterminal, Collection<HeapConfiguration>> getRulesCreatingSelectorFor( Nonterminal nonterminal, 
			int tentacle,
			String selectorName ) {
		
		Map<Nonterminal, Collection<HeapConfiguration> > res = new HashMap<>();
		
		for( Nonterminal nt : nonterminalsInResultingKeySet  ){
			
			res.put(nt, heapsForResult);
		}
		
		return res;
		
	}
	
	public void defineReturnedLhsForTest( Collection<Nonterminal> nonterminalsInResultingKeySet ){
		this.nonterminalsInResultingKeySet = nonterminalsInResultingKeySet;
	}
	
	public void defineRhsForAllNonterminals( Collection<HeapConfiguration> heapsForResult ){
		this.heapsForResult = heapsForResult;
	}

}
