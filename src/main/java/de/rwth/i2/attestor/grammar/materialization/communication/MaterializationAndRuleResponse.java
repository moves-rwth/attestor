package de.rwth.i2.attestor.grammar.materialization.communication;

import java.util.*;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;


public class MaterializationAndRuleResponse implements GrammarResponse {

	private AbstractIndexSymbol symbolToMaterialize;
	private Map<List<IndexSymbol>, Collection<HeapConfiguration>> materializationsAndRules;

	public MaterializationAndRuleResponse(Map<List<IndexSymbol>, Collection<HeapConfiguration>> rules,
			AbstractIndexSymbol indexSymbolToMaterialize ) {
		super();
		this.materializationsAndRules = rules;
		this.symbolToMaterialize = indexSymbolToMaterialize;
	}
	

	public boolean hasIndexSymbolToMaterialize(){
		return symbolToMaterialize != null;
	}
	
	public AbstractIndexSymbol getIndexSymbolToMaterialize(){
		return symbolToMaterialize;
	}
	
	public Set<List<IndexSymbol>> getPossibleMaterializations(){
		return materializationsAndRules.keySet();
	}
	
	public Collection<HeapConfiguration> getRulesForMaterialization( List<IndexSymbol> materialization ){
		return materializationsAndRules.get( materialization );
	}
	
}
