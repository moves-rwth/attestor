package de.rwth.i2.attestor.grammar.materialization.util;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarRequest;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.*;

/**
 * Responsible for computing and caching the rules (lhs &#8594; rhs) in the grammar which resolve a certain
 * violation point.
 *
 * @author Hannah
 */
public class ViolationPointResolver {

    private final GrammarAdapter grammar;

    private final Map<GrammarRequest, Map<Nonterminal, Collection<HeapConfiguration>>>
            ruleGraphsCreatingSelector = new LinkedHashMap<>();

    public ViolationPointResolver(Grammar grammar) {

        this.grammar = new GrammarAdapter(grammar);
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

    	Map<Nonterminal, Collection<HeapConfiguration>> rules = grammar.getAllRulesFor(nonterminal);

    	Iterator<Map.Entry<Nonterminal, Collection<HeapConfiguration>>> iterator = rules.entrySet().iterator();
    	while(iterator.hasNext()) {
    	    Map.Entry<Nonterminal, Collection<HeapConfiguration>> next = iterator.next();
            Collection<HeapConfiguration> rhs = new LinkedList<>( next.getValue() );
            rhs.removeIf( hc -> ! ruleResolvesViolationPoint(hc, tentacle, requiredSelector));
            if( rhs.isEmpty() ){
                iterator.remove();
            }else{
                next.setValue(rhs);
            }
        }

        return rules;
    }
    
    private boolean ruleResolvesViolationPoint( HeapConfiguration hc, int tentacle, String requiredSelector ){
    	 int node = hc.externalNodeAt(tentacle);

         for (SelectorLabel sel : hc.selectorLabelsOf(node)) {

             if (sel.hasLabel(requiredSelector)) {

                 if (hc.selectorTargetOf(node, sel) != HeapConfiguration.INVALID_ELEMENT) {
                    return true;
                 }
                 return false;
             }
         }
         return false;
    }


}
