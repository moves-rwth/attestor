package de.rwth.i2.attestor.grammar.materialization.communication;

import java.util.Collection;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class DefaultGrammarResponse implements GrammarResponse {

	private Collection<HeapConfiguration> applicableRules;
	
	public DefaultGrammarResponse( Collection<HeapConfiguration> rules ){
		applicableRules = rules;
	}
	

	
	public Collection<HeapConfiguration> getApplicableRules(){
		return applicableRules;
	}
	
}
