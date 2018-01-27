package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Immutable data-object storing all the rules (lhs &#8594; rhs).
 * For construction use a {@link GrammarBuilder}
 *
 * @author Hannah
 */
public class Grammar {

    final Map<Nonterminal, Set<HeapConfiguration>> rules;
    final Map<Nonterminal, Set<CollapsedHeapConfiguration>> collapsedRules;

    Grammar(Map<Nonterminal, Set<HeapConfiguration>> rules,
            Map<Nonterminal, Set<CollapsedHeapConfiguration>> collapsedRules) {

        this.rules = rules;
        this.collapsedRules = collapsedRules;
    }

    public static GrammarBuilder builder() {

        return new GrammarBuilder();
    }

    /**
     * Gets all rule graphs of rules with the specified left hand side.
     *
     * @param nonterminal The left hand side
     * @return an unmodifiable view of the rules' set
     */
    public Set<HeapConfiguration> getRightHandSidesFor(Nonterminal nonterminal) {

        if (rules.containsKey(nonterminal)) {
            return Collections.unmodifiableSet(rules.get(nonterminal));
        } else {
            return new LinkedHashSet<>();
        }
    }


    /**
     * @return an unmodifiable view of the set of left hand sides
     */
    public Set<Nonterminal> getAllLeftHandSides() {

        return Collections.unmodifiableSet(rules.keySet());
    }

    public Set<CollapsedHeapConfiguration> getCollapsedRightHandSidesFor(Nonterminal nonterminal) {

        if(!collapsedRules.containsKey(nonterminal)) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(collapsedRules.get(nonterminal));
        }
    }


}
