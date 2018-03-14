package de.rwth.i2.attestor.refinement.reachability;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.RefinedNonterminal;
import de.rwth.i2.attestor.refinement.grammarRefinement.GrammarRefinement;

public class ReachabilityComputer extends SceneObject{
	
	
	public ReachabilityComputer( Scene scene ) {
		super( scene );
	}
	
	/**
	 * computes the reachability between tentacles of nonterminals
	 * given the grammar and stores this information directly in the nonterminals.
	 * 
	 * This method uses the {@link GrammarRefinedment} with the {@link ReachabilityHeapAutomaton} 
	 * @param grammar 
	 */
	public void precomputeReachability( Grammar grammar ) {
    	HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton( scene(), new HashSet<>() );
		GrammarRefinement refinement = new GrammarRefinement( grammar, reachabilityAutomaton  );

		Grammar refinedGrammar = refinement.getRefinedGrammar();
		
		
		for( Nonterminal refinedNt : refinedGrammar.getAllLeftHandSides() ) {
			RefinedNonterminal refinedNonterminal = (RefinedNonterminal) refinedNt;
			Nonterminal nt = scene().getNonterminal( refinedNt.getLabel() );
			
			Map<Integer, Collection<Integer>> reachabilityMap = new HashMap<>();
			for( int i = 0; i < nt.getRank(); i++ ) {
				ReachabilityAutomatonState reachabilityState = (ReachabilityAutomatonState) refinedNonterminal.getState();
				reachabilityMap.put( i, reachabilityState.reachableSetFrom(i) ); 
			}
			nt.setReachableTentacles(reachabilityMap);
		}
		
	}

}
