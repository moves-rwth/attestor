package de.rwth.i2.attestor.grammar.materialization.defaultGrammar;

import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.defaultState.RefinedDefaultNonterminal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Calculates and caches the rules of a basic grammar (i.e. non-indexed) to handle each violationPoint.
 *
 * @author Hannah
 */
public class DefaultMaterializationRuleManager implements MaterializationRuleManager {

    final ViolationPointResolver vioPointResolver;

    public DefaultMaterializationRuleManager(ViolationPointResolver vioResolver) {

        this.vioPointResolver = vioResolver;
    }

    /**
     * returns a DefaultGrammarResponse containing the right hand side of all rules
     * whose lhs matches the given nonterminal and which create the requested selector
     * at the given tentacle of the nonterminal.
     */
    @Override
    public GrammarResponse getRulesFor(Nonterminal toReplace, int tentacle, String requestedSelector)
            throws UnexpectedNonterminalTypeException {

        if (!(toReplace instanceof BasicNonterminal) && !(toReplace instanceof RefinedDefaultNonterminal)) {
            throw new UnexpectedNonterminalTypeException("DefaultMaterializationRuleManager can only deal with " +
                    "DefaultNonterminal and RefinedNonterminalImpl");
        }

        Map<Nonterminal, Collection<HeapConfiguration>> rulesResolvingVioPoint =
                vioPointResolver.getRulesCreatingSelectorFor(toReplace, tentacle, requestedSelector);

        if (rulesResolvingVioPoint.containsKey(toReplace)) {
            Collection<HeapConfiguration> rightHandSidesOfRules = rulesResolvingVioPoint.get(toReplace);
            return new DefaultGrammarResponse(rightHandSidesOfRules);
        } else {
            return new DefaultGrammarResponse(new ArrayList<>());
        }


    }

}
