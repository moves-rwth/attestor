package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.graph.Nonterminal;

public interface MaterializationRuleManager {

	public GrammarResponse getRulesFor( Nonterminal toReplace, int tentacle, String requestedSelector ) throws UnexpectedNonterminalTypeException;
}
