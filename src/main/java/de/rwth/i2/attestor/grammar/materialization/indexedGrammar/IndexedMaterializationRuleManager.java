package de.rwth.i2.attestor.grammar.materialization.indexedGrammar;

import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarRequest;
import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.MaterializationAndRuleResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import gnu.trove.iterator.TIntIterator;

import java.util.*;

/**
 * Computes and caches the rules for resolving a given violation point
 * for {@link IndexedNonterminal}s in addition to all {@link Nonterminal}s
 * handled by {@link DefaultMaterializationRuleManager}.
 * 
 * @author Hannah
 *
 */
public class IndexedMaterializationRuleManager extends DefaultMaterializationRuleManager {

	private ViolationPointResolver violationPointResolver;

	private IndexMatcher indexMatcher;

	private Map<GrammarRequest, GrammarResponse> instantiatedRuleGraphsCreatingSelector = new HashMap<>();

	public IndexedMaterializationRuleManager(ViolationPointResolver vioResolver, IndexMatcher indexMatcher) {
		super( vioResolver );
		this.violationPointResolver = vioResolver;
		this.indexMatcher = indexMatcher;
	}


	@Override
	public GrammarResponse getRulesFor(Nonterminal toReplace, int tentacle, String requestedSelector) 
			throws UnexpectedNonterminalTypeException {

		GrammarRequest request = new GrammarRequest( toReplace, tentacle, requestedSelector );
		GrammarResponse response;

		if( instantiatedRuleGraphsCreatingSelector.containsKey(request) ){
			response = instantiatedRuleGraphsCreatingSelector.get(request);
		}else{

			response = computeResponse(toReplace, tentacle, requestedSelector);
			instantiatedRuleGraphsCreatingSelector.put(request, response);
		}
		return response;
	}


	/**
	 * Computes the Correct Grammar Response for the given request.
	 * For defaultNonterminals the response consists of all rules matching the nonterminal 
	 * and resolving the violation point
	 * For indexedNonterminals the response consists of materializations which have to be applied
	 * to the graph and all rules resolving the violation point which can be applied after this
	 * materialization 
	 * @param toReplace the nonterminal to match
	 * @param tentacle the tentacle of the nonterminal at which the violation point sits
	 * @param requestedSelector the selector creating the violation point
	 * @return the response containing all necessary information 
	 * for materialization of this violation point
	 * @throws UnexpectedNonterminalTypeException
	 */
	private GrammarResponse computeResponse(Nonterminal toReplace, 
											int         tentacle, 
											String      requestedSelector)
	throws UnexpectedNonterminalTypeException{
		
		Map<Nonterminal, Collection<HeapConfiguration> > rulesResolvingViolationPoint = 
				this.violationPointResolver.getRulesCreatingSelectorFor( toReplace, 
																		 tentacle, 
																		 requestedSelector );

		if( toReplace instanceof IndexedNonterminal){
			IndexedNonterminal indexedToReplace = (IndexedNonterminal) toReplace;
			return computeMaterializationsAndRules( indexedToReplace, rulesResolvingViolationPoint);

		}else{
			System.out.println("NOT AN INT!!!");
			return super.getRulesFor( toReplace, tentacle, requestedSelector );
		}
	}


	/**
	 * For each lhs in the suggested rules, determines whether it can match the nonterminal
	 * and adds the necessary materialization and instantiated rules in this case.
	 * 
	 * @param toReplace
	 * @param rulesResolvingViolationPoint
	 * @return
	 */
	private GrammarResponse computeMaterializationsAndRules(IndexedNonterminal toReplace,
			Map<Nonterminal, Collection<HeapConfiguration>> rulesResolvingViolationPoint) {

	

		Map<List<IndexSymbol>, Collection<HeapConfiguration>> allMaterializationsAndRules =
				new HashMap<>();

		for( Nonterminal lhs : rulesResolvingViolationPoint.keySet() ){
			final IndexedNonterminal indexedLhs = (IndexedNonterminal) lhs;
			
			if( indexMatcher.canMatch( toReplace, indexedLhs) ){
			addMaterializationAndRules(allMaterializationsAndRules, 
									   toReplace, indexedLhs, 
									   rulesResolvingViolationPoint.get(lhs) );
			}
		}
		
		AbstractIndexSymbol stackSymbolToMaterialize = getStackSymbolToMaterialize( toReplace );
		
		return new MaterializationAndRuleResponse( allMaterializationsAndRules, 
												   stackSymbolToMaterialize );
	}

	private AbstractIndexSymbol getStackSymbolToMaterialize(IndexedNonterminal toReplace) {

		IndexSymbol lastSymbol = toReplace.getIndex().getLastStackSymbol();
		if( lastSymbol instanceof AbstractIndexSymbol){
			return (AbstractIndexSymbol) lastSymbol;
		}else{
			return null;
		}
	}


	/**
	 * Determines the necessary materialization
	 * and instantiates all rules graphs such that the lhs matches the nonterminal
	 * 
	 * @param allMaterializationsAndRules the resultMap to which the results from this rule will be added
	 * @param toReplace the nonterminal to match
	 * @param lhs the lhs of these rules
	 * @param uninstantiatedRulesForThisLhs  the rules belonging to this lhs
	 */
	private void addMaterializationAndRules(Map<List<IndexSymbol>, Collection<HeapConfiguration>> allMaterializationsAndRules,
			final IndexedNonterminal toReplace, IndexedNonterminal lhs,
			Collection<HeapConfiguration> uninstantiatedRulesForThisLhs ) {
		

			final List<IndexSymbol> necessaryMaterialization =
					indexMatcher.getMaterializationRule(toReplace, lhs).second();
			addMaterializationIfNeceessaryTo(allMaterializationsAndRules, necessaryMaterialization);
			
			Collection<HeapConfiguration> instantiatedRulesForThisLhs = 
					instantiateRulesIfNecessary( toReplace, lhs, 
							uninstantiatedRulesForThisLhs);

			allMaterializationsAndRules.get(necessaryMaterialization).addAll( instantiatedRulesForThisLhs );

	}

	/**
	 * Adds the given materialization to the map if it is not yet present
	 * @param allMaterializationsAndRules the map in which the materialization shall be present
	 * @param necessaryMaterialization the materialization which shall be in the map
	 */
	private void addMaterializationIfNeceessaryTo(Map<List<IndexSymbol>, Collection<HeapConfiguration>> allMaterializationsAndRules,
			final List<IndexSymbol> necessaryMaterialization) {
		
		if( ! allMaterializationsAndRules.containsKey( necessaryMaterialization)){
			allMaterializationsAndRules.put(necessaryMaterialization, new ArrayList<>());
		}
	}

	/**
	 * Checks if the given lhs requires an instantiation and returns either the
	 * already concrete right hand sides or instantiates them appropriately.
	 * 
	 * @param intexedToReplace the nonterminal which has to be matched
	 * @param indexedLhs the left hand side of the rules
	 * @param uninstantiatedRulesForThisLhs the set of rules (may be concrete or uninstantiated)
	 * @return
	 */
	private Collection<HeapConfiguration> instantiateRulesIfNecessary(
								final IndexedNonterminal intexedToReplace,
								final IndexedNonterminal indexedLhs, 
								final Collection<HeapConfiguration> uninstantiatedRulesForThisLhs) 
	{
		
		Collection<HeapConfiguration> instantiatedRulesForThisLhs;
		
		if( indexMatcher.needsInstantiation(intexedToReplace, indexedLhs) ){
			

			final List<IndexSymbol> necessaryInstantiation =
					indexMatcher.getNecessaryInstantiation(intexedToReplace, indexedLhs);
			instantiatedRulesForThisLhs = instantiateRhs( necessaryInstantiation, uninstantiatedRulesForThisLhs );
		}else{
			instantiatedRulesForThisLhs = uninstantiatedRulesForThisLhs;
		}
		return instantiatedRulesForThisLhs;
	}

	/**
	 * instantiates all nonterminals in the graph which require this.
	 * For example if a graph contains the two indexed nonterminals Nt[s,()] and Nt[Z]
	 * and the given instantiation is [s,Z], then the graph will afterwards contain
	 * the two nonterminals Nt[s,s,Z] and Nt[Z] 
	 * 
	 * @param necessaryInstantiation the instantiation which will be applied
	 * @param uninstantiatedRules the rules that (may) require instantiation
	 * @return a collection containing all graphs, now with fully instantiated stacks.
	 */
	private Collection<HeapConfiguration> instantiateRhs(List<IndexSymbol> necessaryInstantiation,
			Collection<HeapConfiguration> uninstantiatedRules) {
		Collection<HeapConfiguration> res = new ArrayList<>();

		for( HeapConfiguration uninstantiatedRhs : uninstantiatedRules ){
			HeapConfigurationBuilder builder = uninstantiatedRhs.clone().builder();
			TIntIterator edgeIter = uninstantiatedRhs.nonterminalEdges().iterator();
			while(edgeIter.hasNext()) {
				int e = edgeIter.next();
				
				IndexedNonterminal label = (IndexedNonterminal) uninstantiatedRhs.labelOf(e);
				if( ! label.getIndex().hasConcreteStack() ){
					builder.replaceNonterminal(e, label.getWithProlongedStack( necessaryInstantiation ));
				}
			}
			HeapConfiguration instantiatedRule = builder.build();
			res.add( instantiatedRule );
		}
		return res;
	}




}
