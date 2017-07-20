package de.rwth.i2.attestor.io;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

public class JsonToIndexedGrammar {
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "JsonToGrammar" );

	public static Map<Nonterminal, Collection<HeapConfiguration>>
	parseForwardGrammar( JSONArray input ){

		Map<Nonterminal, Collection<HeapConfiguration>> res = new HashMap<>();
		List<IndexedNonterminal> ntsWithoutReductionTentacles = new ArrayList<>();

		for( int i = 0; i < input.length(); i++){

			JSONObject grammarFragment = input.getJSONObject( i );

			int rank = getRank(grammarFragment);
			String label = getLabel(grammarFragment);
			List<StackSymbol> stack = getStack(grammarFragment);
			IndexedNonterminal nt;

			if( hasDefinedTentacles(grammarFragment) ) {

				nt = new IndexedNonterminal(label, rank, getReductionTentacles(grammarFragment), stack);

			} else {

				boolean[] rts = new boolean[rank];
				Arrays.fill(rts, false);
				nt = new IndexedNonterminal(label, rank, rts, stack);
				ntsWithoutReductionTentacles.add(nt);
			}

			res.put( nt,  getGraphs(nt, grammarFragment) );
		}

		updateReductionTentacles(ntsWithoutReductionTentacles, res); 

		return res;
	}

	private static int getRank( JSONObject grammarFragment ) {

		return grammarFragment.getInt( "rank" );
	}

	private static String getLabel( JSONObject grammarFragment ) {

		return grammarFragment.getString( "nonterminal" );
	}
	
	private static List<StackSymbol> getStack( JSONObject grammarFragment ){
		JSONArray stack = grammarFragment.getJSONArray("stack");
		return JsonToIndexedHC.parseStack(stack);
	}

	private static boolean hasDefinedTentacles(JSONObject grammarFragment) {

		return grammarFragment.has( "redundandTentacles" );
	}

	private static boolean[] getReductionTentacles( JSONObject grammarFragment ) {

		JSONArray tentacles = grammarFragment.getJSONArray( "redundandTentacles" );
		boolean[] res = new boolean[tentacles.length()];
		for( int i = 0; i < tentacles.length(); i++ ) {

			res[i] = tentacles.getBoolean(i);
		}
		return res;	
	}

	private static Set<HeapConfiguration> 
	getGraphs(IndexedNonterminal nt, JSONObject grammarFragment) {

		Set<HeapConfiguration> res = new HashSet<>();
		JSONArray graphs = grammarFragment.getJSONArray( "rules" );

		for( int g = 0; g < graphs.length(); g++ ){

			res.add( JsonToIndexedHC.jsonToHC( graphs.getJSONObject( g ) ) );
		}

		return res;
	}

	private static void updateReductionTentacles(List<IndexedNonterminal> ntsWithoutReductionTentacles,
                                                 Map<Nonterminal, Collection<HeapConfiguration>> res) {

		Deque<Pair<IndexedNonterminal, Integer>> changedTentacles = new  ArrayDeque<>();
		Map<Pair<IndexedNonterminal,Integer>,Set<Pair<IndexedNonterminal,Integer>>> adjacentNonterminals = new HashMap<>();//captures the nonterminals which have to be revisited on a change of the key nonterminal

		//init - set all tentacles to redactionTentacles
		initializeToReductionTentacles( ntsWithoutReductionTentacles );

		setSimpleNonRedactionTentacles( ntsWithoutReductionTentacles, res, changedTentacles );

		rememberAdjacentTentacles( ntsWithoutReductionTentacles, res, adjacentNonterminals );

		//call this to also consider nonterminals for which we already know the reductionTentacles
		computeEffectOfAdjacentTentacles( changedTentacles, adjacentNonterminals );
		computeFixpointOfReductionTentacles( changedTentacles, adjacentNonterminals );

	}

	/**
	 * Propagates the reduction-status of all tentacles in the map adjacentNonterminals.
	 * should be called to ensure that also the status of tentacles from nonterminals whose
	 * status is predefined is considered.
	 * @param changedTentacles a deque to remember all tentacles who change to nonReduction
	 * so that they can propagate their change to adjacent tentacles
	 * @param adjacentNonterminals a map that stores which tentacles can affect which other tentacles
	 */
	private static void computeEffectOfAdjacentTentacles( Deque<Pair<IndexedNonterminal, Integer>> changedTentacles,
			Map<Pair<IndexedNonterminal, Integer>, Set<Pair<IndexedNonterminal, Integer>>> adjacentNonterminals ) {
		for(Pair<IndexedNonterminal, Integer> tentacle : adjacentNonterminals.keySet() ){
			if( !tentacle.first().isReductionTentacle( tentacle.second() ) ){
				for( Pair<IndexedNonterminal, Integer> affected : adjacentNonterminals.get( tentacle ) ){
					if( affected.first().isReductionTentacle( affected.second() )){
						affected.first().unsetReductionTentacle( affected.second() );
						changedTentacles.add( affected );
					}
				}
			}
		}
	}

	/**
	 * uses the deque of changedTentacles to update their adjacentTentacles
	 * until no new changes occur
	 * @param changedTentacles a deque of tentacles which changed from reduction to nonReduction
	 * (they are removed from the deque when their effect on adjacent tentacles is computed)
	 * @param adjacentNonterminals a map from tentacles to a set of tentacles which are affected
	 * by their reduction-status
	 */
	private static void computeFixpointOfReductionTentacles(
			Deque<Pair<IndexedNonterminal, Integer>> changedTentacles,
			Map<Pair<IndexedNonterminal, Integer>, Set<Pair<IndexedNonterminal, Integer>>> adjacentNonterminals ) {

		while( ! changedTentacles.isEmpty() ){
			Pair<IndexedNonterminal, Integer> changedTentacle = changedTentacles.pop();
			if( adjacentNonterminals.containsKey( changedTentacle ) ){
				for( Pair<IndexedNonterminal, Integer> affected : adjacentNonterminals.get( changedTentacle ) ){
					if( affected.first().isReductionTentacle( affected.second() ) ){
						affected.first().unsetReductionTentacle( affected.second() );
						changedTentacles.add( affected );
					}
				}
			}
		}
	}

	/**
	 * computes a map from tentacles to sets tentacles such that after a tentacle changed
	 * to nonReductionTentacle (i.e. we computed that it can produce an outgoing selector)
	 * we only have to update the tentacles in its associated set. 
	 * @param ntsWithoutReductionTentacles the nonterminals to consider
	 * @param res the rules of the grammar considered
	 * @param adjacentNonterminals the map of adjacentTentacles to which the result will be stored
	 */
	private static void rememberAdjacentTentacles( List<IndexedNonterminal> ntsWithoutReductionTentacles,
			Map<Nonterminal, Collection<HeapConfiguration>> res,
			Map<Pair<IndexedNonterminal, Integer>, Set<Pair<IndexedNonterminal, Integer>>> adjacentNonterminals ) {

		for( IndexedNonterminal nt : ntsWithoutReductionTentacles ){
			Collection<HeapConfiguration> rulesForNt = res.get( nt );
			for(int i=0; i < nt.getRank(); i++ ){

				if( nt.isReductionTentacle( i ) ){
					findAdjacentTentaclesFor( nt, i, rulesForNt, adjacentNonterminals );
				}
			}
		}
	}

	/**
	 * computes the adjacent tentacles to nonterminal nt at tentacle i for any of
	 * its rhs in rules
	 * @param nt the nonterminal to consider
	 * @param i the index of the tentacle to consider
	 * @param rulesForNt the set of rules for the considered nonterminal
	 * @param adjacentNonterminals the map in which the adjacentNonterminals are stored
	 */
	private static void findAdjacentTentaclesFor( IndexedNonterminal nt, int i,
			Collection<HeapConfiguration> rulesForNt,
			Map<Pair<IndexedNonterminal, Integer>, Set<Pair<IndexedNonterminal, Integer>>> adjacentNonterminals ) {

		for( HeapConfiguration hc : rulesForNt ){

			int externalNode = hc.externalNodeAt( i );
			
			TIntArrayList attachedNts = hc.attachedNonterminalEdgesOf(externalNode);
			TIntIterator iter = attachedNts.iterator();
			
			while(iter.hasNext()) {
				
				int adjacentNonterminalEdge = iter.next();
				IndexedNonterminal adjacentNonterminal = (IndexedNonterminal) hc.labelOf( adjacentNonterminalEdge );
				TIntArrayList attachedNodes = hc.attachedNodesOf(adjacentNonterminalEdge);
				
				for( int t = 0; t < adjacentNonterminal.getRank(); t++ ){
					if( attachedNodes.get( t ) == externalNode ){
						Pair<IndexedNonterminal, Integer> pair = new Pair<>( adjacentNonterminal, t );
						if( ! adjacentNonterminals.containsKey( pair )){
							adjacentNonterminals.put( pair, new HashSet<>() );
						}
						adjacentNonterminals.get( pair ).add(new Pair<>(nt, i) );
					}
				}

			}
		}
	}

	/**
	 * Sets those tentacles to nonReductionTentacles which directly produce an outgoing selector
	 * @param ntsWithoutReductionTentacles the nonterminals to consider
	 * @param res the rules in the grammar which may produce the outgoing selectors
	 * @param changedTentacles stores those tentacles for which a change occured to consider them
	 * in the fixpoint computation
	 */
	private static void setSimpleNonRedactionTentacles( List<IndexedNonterminal> ntsWithoutReductionTentacles,
			Map<Nonterminal, Collection<HeapConfiguration>> res,
			Deque<Pair<IndexedNonterminal, Integer>> changedTentacles ) {

		for( IndexedNonterminal nt : ntsWithoutReductionTentacles ){
			for(int i=0; i < nt.getRank(); i++ ){
				if( nt.isReductionTentacle( i ) ){
					Collection<HeapConfiguration> rulesForNt = res.get( nt );
					computeSimpleNonReductionTentaclesFor( nt, i, rulesForNt, changedTentacles );
				}
			}
		}
	}

	/**
	 * Sets the tentacle i of nonterminal nt to nonReduction if there is a rule for nt
	 * which directly produces an outgoing selector at external i
	 * @param nt the nonterminal to consider
	 * @param i the tentacle to consider
	 * @param rulesForNt the grammar rules for nonterminal nt (nt on lhs)
	 * @param changedTentacles if the tentacle changed to NonReduction it will be added to this
	 * stack so that it is later propagated to tentacles it is adjacent to
	 */
	private static void computeSimpleNonReductionTentaclesFor( IndexedNonterminal nt, int i,
			Collection<HeapConfiguration> rulesForNt,
			Deque<Pair<IndexedNonterminal, Integer>> changedTentacles ) {
		
		for( HeapConfiguration hc : rulesForNt ){
			int externalNode = hc.externalNodeAt( i );
			
			if(hc.selectorLabelsOf(externalNode).size() > 0) {
			
				changedTentacles.add(new Pair<>(nt, i) );
				nt.unsetReductionTentacle( i );
			}
		}
	}

	/**
	 * sets all tentacles of all nonterminals in the set to reductionTentacles
	 * @param ntsWithoutReductionTentacles the nonterminals whose tentacles are set to reductionTentacles
	 */
	private static void initializeToReductionTentacles( List<IndexedNonterminal> ntsWithoutReductionTentacles ) {
		for( IndexedNonterminal nt : ntsWithoutReductionTentacles ){
			for( int i = 0; i < nt.getRank(); i++ ){
				nt.setReductionTentacle( i );
			}
		}
	}

}
