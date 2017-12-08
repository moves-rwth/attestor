package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarRequest;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Responsible for computing and caching the rules (lhs &#8594; rhs) in the grammar which resolve a certain
 * violation point.
 *
 * @author Hannah
 */
public class ViolationPointResolver {

    private final Grammar grammar;

    private final Map<GrammarRequest, Map<Nonterminal, Collection<HeapConfiguration>>>
            ruleGraphsCreatingSelector = new LinkedHashMap<>();

    public ViolationPointResolver(Grammar grammar) {

        this.grammar = grammar;
    }

    /**
     * gets all rules matching the given nonterminal and creating the requested selector label
     * at the given tentacle of the nonterminal
     *
     * @param nonterminal  the nonterminal to replace
     * @param tentacle     the tentacle of the nonterminal at which the selector Label is requested
     * @param selectorName the name of the requested selector Label
     * @return the rules in form lhs &#8594; {rhs}
     */
    public Map<Nonterminal, Collection<HeapConfiguration>> getRulesCreatingSelectorFor(
            Nonterminal nonterminal,
            int tentacle,
            String selectorName) {

        GrammarRequest request = new GrammarRequest(nonterminal, tentacle, selectorName);
        Map<Nonterminal, Collection<HeapConfiguration>> rules;

        if (ruleGraphsCreatingSelector.containsKey(request)) {
            rules = ruleGraphsCreatingSelector.get(request);
        } else {

            rules =
                    computeRulesCreatingSelector(nonterminal, tentacle, selectorName);
            ruleGraphsCreatingSelector.put(request, rules);
        }

        return rules;
    }


    /**
     * checks for all left hand sides in the grammar whether they match the given nonterminal
     * and adds for the matching ones those right hand sides creating the requested selector at
     * the requested tentacle of the nonterminal
     *
     * @param nonterminal      the nonterminal to match
     * @param tentacle         the tentacle at which the selector shall be created
     * @param requiredSelector the name of the requested selector
     * @return the result set containing all possible lhs with the appropriate rhs'
     */
    private Map<Nonterminal, Collection<HeapConfiguration>> computeRulesCreatingSelector(
            Nonterminal nonterminal, int tentacle, String requiredSelector) {

        Map<Nonterminal, Collection<HeapConfiguration>> res = new LinkedHashMap<>();

        for (Nonterminal grammarNt : grammar.getAllLeftHandSides()) {

            if (grammarNt.getLabel().equals(nonterminal.getLabel())) {

                Collection<HeapConfiguration> rulesForGrammarNt =
                        getRuleGraphsResolvingViolationPointFor(grammarNt,
                                tentacle, requiredSelector);
                if (!rulesForGrammarNt.isEmpty()) {
                    res.put(grammarNt, rulesForGrammarNt);
                }

            }
        }

        return res;
    }

    /**
     * For a given lhs (a nonterminal in the grammar) computes all rules creating the
     * requested selector at the requested tentacle of the nonterminal
     *
     * @param grammarNt        a lhs in the grammar
     * @param tentacle         the tentacle of the nonterminal (i.e. external node of the rhs)
     *                         at which the selector is requested
     * @param requiredSelector the name of the requested selector
     * @return the set of all rhs for this lhs which create the selector at the tentacle
     */
    private Collection<HeapConfiguration> getRuleGraphsResolvingViolationPointFor(
            Nonterminal grammarNt,
            int tentacle,
            String requiredSelector) {

        Collection<HeapConfiguration> rulesForGrammarNt = new ArrayList<>();


        for (HeapConfiguration hc : grammar.getRightHandSidesFor(grammarNt)) {


            int node = hc.externalNodeAt(tentacle);

            for (SelectorLabel sel : hc.selectorLabelsOf(node)) {

                if (sel.hasLabel(requiredSelector)) {

                    if (hc.selectorTargetOf(node, sel) != HeapConfiguration.INVALID_ELEMENT) {
                        rulesForGrammarNt.add(hc);
                    }
                    break;
                }
            }

        }
        return rulesForGrammarNt;
    }


}
