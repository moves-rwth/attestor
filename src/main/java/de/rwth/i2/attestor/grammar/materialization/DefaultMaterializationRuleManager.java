package de.rwth.i2.attestor.grammar.materialization;

import java.util.*;

import de.rwth.i2.attestor.grammar.materialization.communication.*;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;

/**
 * Calculates and caches the rules of a basic grammar (i.e. non-indexed) to handle each violationPoint.
 * @author Hannah
 *
 */
public class DefaultMaterializationRuleManager implements MaterializationRuleManager {

	ViolationPointResolver vioPointResolver;
	
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
		
		if( !( toReplace instanceof GeneralNonterminal) ){
			throw new UnexpectedNonterminalTypeException("DefaultMaterializationRuleManager can only deal with DefaultNonterminals");
		}
		
		Map<Nonterminal, Collection<HeapConfiguration>> rulesResolvingVioPoint =
				vioPointResolver.getRulesCreatingSelectorFor(toReplace, tentacle, requestedSelector);
		
		if( rulesResolvingVioPoint.containsKey(toReplace) ){
			Collection<HeapConfiguration> rightHandSidesOfRules = rulesResolvingVioPoint.get( toReplace );
			return new DefaultGrammarResponse( rightHandSidesOfRules );
		}else{
			return new DefaultGrammarResponse( new ArrayList<>() );
		}
		
		
		
	}

}
