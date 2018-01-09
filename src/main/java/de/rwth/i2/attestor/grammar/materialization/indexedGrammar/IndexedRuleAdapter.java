package de.rwth.i2.attestor.grammar.materialization.indexedGrammar;

import java.util.*;

import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.MaterializationAndRuleResponse;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import gnu.trove.iterator.TIntIterator;

public class IndexedRuleAdapter {
	public IndexMatcher indexMatcher;

	public IndexedRuleAdapter(IndexMatcher indexMatcher) {
		this.indexMatcher = indexMatcher;
	}

	/**
	 * For each lhs in the suggested rules, determines whether it can match the nonterminal
	 * and adds the necessary materialization and instantiated rules in this case.
	 *
	 * @param toReplace
	 * @param rulesResolvingViolationPoint
	 * @return
	 */
	public GrammarResponse computeMaterializationsAndRules(IndexedNonterminal toReplace, Map<Nonterminal, Collection<HeapConfiguration>> rulesResolvingViolationPoint) {
	
	
	    Map<List<IndexSymbol>, Collection<HeapConfiguration>> allMaterializationsAndRules =
	            new LinkedHashMap<>();
	
	    for (Nonterminal lhs : rulesResolvingViolationPoint.keySet()) {
	        final IndexedNonterminal indexedLhs = (IndexedNonterminal) lhs;
	
	        if (indexMatcher.canMatch(toReplace, indexedLhs)) {
	            		addMaterializationAndRules(allMaterializationsAndRules,
	                    toReplace, indexedLhs,
	                    rulesResolvingViolationPoint.get(lhs));
	        }
	    }
	
	    AbstractIndexSymbol indexSymbolToMaterialize = getIndexSymbolToMaterialize(toReplace);
	
	    return new MaterializationAndRuleResponse(allMaterializationsAndRules,
	            indexSymbolToMaterialize);
	}

	/**
	 * Determines the necessary materialization
	 * and instantiates all rules graphs such that the lhs matches the nonterminal
	 *
	 * @param allMaterializationsAndRules   the resultMap to which the results from this rule will be added
	 * @param toReplace                     the nonterminal to match
	 * @param lhs                           the lhs of these rules
	 * @param uninstantiatedRulesForThisLhs the rules belonging to this lhs
	 */
	private void addMaterializationAndRules( Map<List<IndexSymbol>, Collection<HeapConfiguration>> allMaterializationsAndRules, final IndexedNonterminal toReplace, IndexedNonterminal lhs, Collection<HeapConfiguration> uninstantiatedRulesForThisLhs) {
	
	
	    final List<IndexSymbol> necessaryMaterialization =
	            indexMatcher.getMaterializationRule(toReplace, lhs).second();
	    		addMaterializationIfNeceessaryTo(allMaterializationsAndRules, necessaryMaterialization);
	
	    Collection<HeapConfiguration> instantiatedRulesForThisLhs =
	            		instantiateRulesIfNecessary(toReplace, lhs, uninstantiatedRulesForThisLhs);
	
	    allMaterializationsAndRules.get(necessaryMaterialization).addAll(instantiatedRulesForThisLhs);
	
	}

	/**
	 * Adds the given materialization to the map if it is not yet present
	 *
	 * @param allMaterializationsAndRules the map in which the materialization shall be present
	 * @param necessaryMaterialization    the materialization which shall be in the map
	 */
	private void addMaterializationIfNeceessaryTo(Map<List<IndexSymbol>, Collection<HeapConfiguration>> allMaterializationsAndRules, final List<IndexSymbol> necessaryMaterialization) {
	
	    if (!allMaterializationsAndRules.containsKey(necessaryMaterialization)) {
	        allMaterializationsAndRules.put(necessaryMaterialization, new ArrayList<>());
	    }
	}

	AbstractIndexSymbol getIndexSymbolToMaterialize(IndexedNonterminal toReplace) {
	
	    IndexSymbol lastSymbol = toReplace.getIndex().getLastIndexSymbol();
	    if (lastSymbol instanceof AbstractIndexSymbol) {
	        return (AbstractIndexSymbol) lastSymbol;
	    } else {
	        return null;
	    }
	}

	/**
	 * Checks if the given lhs requires an instantiation and returns either the
	 * already concrete right hand sides or instantiates them appropriately.
	 *
	 * @param intexedToReplace              the nonterminal which has to be matched
	 * @param indexedLhs                    the left hand side of the rules
	 * @param uninstantiatedRulesForThisLhs the set of rules (may be concrete or uninstantiated)
	 * @return
	 */
	private Collection<HeapConfiguration> instantiateRulesIfNecessary( final IndexedNonterminal intexedToReplace, final IndexedNonterminal indexedLhs, final Collection<HeapConfiguration> uninstantiatedRulesForThisLhs) {
	
	    Collection<HeapConfiguration> instantiatedRulesForThisLhs;
	
	    if (indexMatcher.needsInstantiation(intexedToReplace, indexedLhs)) {
	
	
	        final List<IndexSymbol> necessaryInstantiation =
	                indexMatcher.getNecessaryInstantiation(intexedToReplace, indexedLhs);
	        instantiatedRulesForThisLhs = instantiateRhs(necessaryInstantiation, uninstantiatedRulesForThisLhs);
	    } else {
	        instantiatedRulesForThisLhs = uninstantiatedRulesForThisLhs;
	    }
	    return instantiatedRulesForThisLhs;
	}

	/**
	 * instantiates all nonterminals in the graph which require this.
	 * For example if a graph containsSubsumingState the two indexed nonterminals Nt[s,()] and Nt[Z]
	 * and the given instantiation is [s,Z], then the graph will afterwards contain
	 * the two nonterminals Nt[s,s,Z] and Nt[Z]
	 *
	 * @param necessaryInstantiation the instantiation which will be applied
	 * @param uninstantiatedRules    the rules that (may) require instantiation
	 * @return a collection containing all graphs, now with fully instantiated indices.
	 */
	private Collection<HeapConfiguration> instantiateRhs(List<IndexSymbol> necessaryInstantiation, Collection<HeapConfiguration> uninstantiatedRules) {
	
	    Collection<HeapConfiguration> res = new ArrayList<>();
	
	    for (HeapConfiguration uninstantiatedRhs : uninstantiatedRules) {
	        HeapConfigurationBuilder builder = uninstantiatedRhs.clone().builder();
	        TIntIterator edgeIter = uninstantiatedRhs.nonterminalEdges().iterator();
	        while (edgeIter.hasNext()) {
	            int e = edgeIter.next();
	
	            IndexedNonterminal label = (IndexedNonterminal) uninstantiatedRhs.labelOf(e);
	            if (!label.getIndex().hasConcreteIndex()) {
	                builder.replaceNonterminal(e, label.getWithProlongedIndex(necessaryInstantiation));
	            }
	        }
	        HeapConfiguration instantiatedRule = builder.build();
	        res.add(instantiatedRule);
	    }
	    return res;
	}
}