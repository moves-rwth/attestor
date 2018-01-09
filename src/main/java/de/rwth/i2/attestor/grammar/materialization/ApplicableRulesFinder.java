package de.rwth.i2.attestor.grammar.materialization;

import java.util.ArrayDeque;
import java.util.Deque;

import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class ApplicableRulesFinder {
	

	public Deque<Pair<Integer, GrammarResponse>> findApplicableRules(HeapConfiguration heapConfiguration) {
		Deque<Pair<Integer,GrammarResponse>> applicableRules = new ArrayDeque<>();
		
		TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
		for( int i = 0; i < ntEdges.size(); i++ ){
			final int ntEdge = ntEdges.get(i);
			Nonterminal nonterminal = heapConfiguration.labelOf(ntEdge );
			GrammarResponse rules = null;//getMatchingRules(nonterminal);
			applicableRules.add(new Pair<>(ntEdge, rules) );
		}
		
		return applicableRules;
	}


}
