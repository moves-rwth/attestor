package de.rwth.i2.attestor.grammar.materialization.util;

import java.util.*;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class GrammarAdapter {
	
	Grammar grammar;

    public GrammarAdapter(Grammar grammar) {
		super();
		this.grammar = grammar;
	}



	public Map<Nonterminal, Collection<HeapConfiguration>> getAllRulesFor(Nonterminal nonterminal) {

        Map<Nonterminal, Collection<HeapConfiguration>> res = new LinkedHashMap<>();

        for (Nonterminal grammarNt : grammar.getAllLeftHandSides()) {

            if (grammarNt.getLabel().equals(nonterminal.getLabel())) {

                Collection<HeapConfiguration> rulesForGrammarNt = grammar.getRightHandSidesFor(grammarNt);
                        
                if (!rulesForGrammarNt.isEmpty()) {
                    res.put(grammarNt, rulesForGrammarNt);
                }

            }
        }

        return res;
    }

}
