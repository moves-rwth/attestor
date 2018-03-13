package de.rwth.i2.attestor.refinement.reachability;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.RefinedDefaultNonterminal;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.grammarRefinement.GrammarRefinement;

public class ReachabilityComputer extends SceneObject{
	
	
	public ReachabilityComputer( Scene scene ) {
		super( scene );
	}
	
	public void precomputeReachability( Grammar grammar ) {
    	HeapAutomaton reachabilityAutomaton = new ReachabilityHeapAutomaton( scene(), new HashSet<>() );
		GrammarRefinement refinement = new GrammarRefinement( grammar, reachabilityAutomaton  );

		Grammar refinedGrammar = refinement.getRefinedGrammar();
		
		//to retrieve the refinedNonterminals corresponding to the original ones
		Map<Nonterminal,Nonterminal> refinedNonterminals = new HashMap<>();
		refinedGrammar.getAllLeftHandSides().forEach( x -> refinedNonterminals.put(x, x));
		
		for( Nonterminal lhs : grammar.getAllLeftHandSides() ) {
			RefinedDefaultNonterminal refinedNonterminal = (RefinedDefaultNonterminal) refinedNonterminals.get(lhs);
			
			Map<Integer, Collection<Integer>> reachabilityMap = new HashMap<>();
			for( int i = 0; i < lhs.getRank(); i++ ) {
				ReachabilityAutomatonState reachabilityState = (ReachabilityAutomatonState) refinedNonterminal.getState();
				reachabilityMap.put( i, reachabilityState.reachableSetFrom(i) ); 
			}
		}
		
	}

}
