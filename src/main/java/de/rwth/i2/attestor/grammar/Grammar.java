package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable data-object storing all the rules (lhs &#8594; rhs).
 * For construction use a {@link GrammarBuilder}
 *
 * @author Hannah
 */
public class Grammar {

    final Map<Nonterminal, List<HeapConfiguration>> rules;
    final Map<Nonterminal, List<CollapsedHeapConfiguration>> collapsedRules;

    Grammar(Map<Nonterminal, List<HeapConfiguration>> rules,
            Map<Nonterminal, List<CollapsedHeapConfiguration>> collapsedRules) {

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
            return Collections.unmodifiableSet(new LinkedHashSet<>(rules.get(nonterminal)));
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

        if (!collapsedRules.containsKey(nonterminal)) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(new LinkedHashSet<>(collapsedRules.get(nonterminal)));
        }
    }

    public int getRulePosition(Nonterminal nt, HeapConfiguration rule) {
        if (!rules.containsKey(nt)) {
            return -1;
        }

        HeapConfiguration found = rules.get(nt).stream()
                .filter(hc -> hc.equals(rule))
                .findFirst().orElse(null);

        return found != null ? rules.get(nt).indexOf(found) : -1;
    }

    public int getCollapsedRulePosition(Nonterminal nt, HeapConfiguration rule) {
        if (!collapsedRules.containsKey(nt)) {
            return -1;
        }

        HeapConfiguration found = collapsedRules.get(nt).stream()
                .filter(hc -> hc.getCollapsed().equals(rule))
                .map(CollapsedHeapConfiguration::getOriginal)
                .findFirst().orElse(null);

        return found != null ? rules.get(nt).indexOf(found) : -1;
    }
}
