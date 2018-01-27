package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.*;

/**
 * Used to construct a grammar.
 * By default, the grammar is not marked as backwards confluent.
 * Use setConfluent() to change this.
 *
 * @author Hannah
 */
public class GrammarBuilder {

    final Map<Nonterminal, Set<HeapConfiguration>> rules = new LinkedHashMap<>();
    final Map<Nonterminal, Set<CollapsedHeapConfiguration>> collapsedRules = new LinkedHashMap<>();

    public Grammar build() {

        return new Grammar(rules, collapsedRules);
    }

    public GrammarBuilder addRule(Nonterminal lhs, HeapConfiguration rhs) {

        if (!rules.containsKey(lhs)) {
            rules.put(lhs, new LinkedHashSet<>());
        }

        rules.get(lhs).add(rhs);

        return this;
    }

    public GrammarBuilder addRules(Nonterminal lhs, Collection<HeapConfiguration> rightHandSides) {

        if (!rules.containsKey(lhs)) {
            rules.put(lhs, new LinkedHashSet<>());
        }
        rules.get(lhs).addAll(rightHandSides);
        return this;
    }

    public GrammarBuilder addRules(Map<Nonterminal, ? extends Collection<HeapConfiguration>> newRules) {

        for (Map.Entry<Nonterminal, ? extends Collection<HeapConfiguration>> ruleEntry : newRules.entrySet()) {
            this.addRules(ruleEntry.getKey(), ruleEntry.getValue());
        }
        return this;
    }

    public GrammarBuilder addCollapsedRule(Nonterminal lhs, CollapsedHeapConfiguration rhs) {

        if (!collapsedRules.containsKey(lhs)) {
            collapsedRules.put(lhs, new LinkedHashSet<>());
        }
        collapsedRules.get(lhs).add(rhs);
        return this;

    }

}
