package de.rwth.i2.attestor.grammar.materialization.util;

import java.util.*;

import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedRuleAdapter;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class ApplicableRulesFinder {
	
	GrammarAdapter grammar;
	IndexedRuleAdapter indexRuleAdapter;
	
	public ApplicableRulesFinder(GrammarAdapter grammar, IndexedRuleAdapter indexRuleAdapter) {
		super();
		this.grammar = grammar;
		this.indexRuleAdapter = indexRuleAdapter;
	}

	public Deque<Pair<Integer, GrammarResponse>> findApplicableRules(HeapConfiguration heapConfiguration) {
		Deque<Pair<Integer,GrammarResponse>> applicableRules = new ArrayDeque<>();
		
		TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
		for( int i = 0; i < ntEdges.size(); i++ ){
			final int ntEdge = ntEdges.get(i);
			Nonterminal nonterminal = heapConfiguration.labelOf(ntEdge );
			GrammarResponse rules = getMatchingRules(nonterminal);
			applicableRules.add(new Pair<>(ntEdge, rules) );
		}
		
		return applicableRules;
	}

	private GrammarResponse getMatchingRules(Nonterminal nonterminal) {
		
		Map<Nonterminal, Collection<HeapConfiguration>> rules = grammar.getAllRulesFor(nonterminal);
		
		if( nonterminal.getClass() == IndexedNonterminal.class ){
			IndexedNonterminal toReplace = (IndexedNonterminal) nonterminal;
			return indexRuleAdapter.computeMaterializationsAndRules(toReplace, rules);
		}else{
			return new DefaultGrammarResponse(rules.get(nonterminal));
		}
	}


}
