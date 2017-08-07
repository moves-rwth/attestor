package de.rwth.i2.attestor.grammar.materialization.communication;

import java.util.*;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.AbstractStackSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackSymbol;

public class MaterializationAndRuleResponse implements GrammarResponse {

	private AbstractStackSymbol symbolToMaterialize;
	private Map<List<StackSymbol>, Collection<HeapConfiguration>> materializationsAndRules;

	public MaterializationAndRuleResponse(Map<List<StackSymbol>, Collection<HeapConfiguration>> rules,
			AbstractStackSymbol stackSymbolToMaterialize ) {
		super();
		this.materializationsAndRules = rules;
		this.symbolToMaterialize = stackSymbolToMaterialize;
	}
	

	public boolean hasStackSymbolToMaterialize(){
		return symbolToMaterialize != null;
	}
	
	public AbstractStackSymbol getStackSymbolToMaterialize(){
		return symbolToMaterialize;
	}
	
	public Set<List<StackSymbol>> getPossibleMaterializations(){
		return materializationsAndRules.keySet();
	}
	
	public Collection<HeapConfiguration> getRulesForMaterialization( List<StackSymbol> materialization ){
		return materializationsAndRules.get( materialization );
	}
	

	
	
}
