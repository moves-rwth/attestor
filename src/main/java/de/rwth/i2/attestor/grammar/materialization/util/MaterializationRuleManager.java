package de.rwth.i2.attestor.grammar.materialization.util;

import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.graph.Nonterminal;

/**
 * Responsible for computing and caching the rules to resolve any given violation point.
 *
 * @author Hannah
 */
public interface MaterializationRuleManager {

    GrammarResponse getRulesFor(Nonterminal toReplace, int tentacle, String requestedSelector) throws UnexpectedNonterminalTypeException;
}
