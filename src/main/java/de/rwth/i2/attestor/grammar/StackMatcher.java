package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.*;
import de.rwth.i2.attestor.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackMatcher {
	
	private Map<Pair<IndexedNonterminal, IndexedNonterminal>, Pair<List<StackSymbol>, List<StackSymbol>>>
		knownMatches = new HashMap<>();

	private StackMaterializationStrategy stackGrammar;
	
	public StackMatcher(StackMaterializationStrategy stackGrammar) {
		this.stackGrammar = stackGrammar;
	}

	/**
	 * Determines whether there is a materialization and an instantiation such that the
	 * stacks of the given nonterminals are equal.
	 * 
	 * @param materializableNonterminal a nonterminal with a (possibly) abstract stack
	 * @param instantiableNonterminal a nonterminal with a (possibly) instantiable stack
	 * @return true if there is a materialization and an instantiation such that the
	 * stacks of the given nonterminals are equal.
	 */
	public boolean canMatch(IndexedNonterminal materializableNonterminal, 
								   IndexedNonterminal instantiableNonterminal ) {
		Pair<IndexedNonterminal, IndexedNonterminal> requestPair =
				new Pair<>(materializableNonterminal, instantiableNonterminal);
		
		if( ! knownMatches.containsKey(requestPair) ){
			computeMatch(materializableNonterminal, instantiableNonterminal);
		}
		return knownMatches.get(requestPair) != null;
	}

	/**
	 * Determines whether the materializableNonterminal has to be materialized to achieve equal stacks
	 * @param materializableNonterminal the nonterminal in the graph which shall be replaced
	 * @param instantiableNonterminal the nonterminal representing the lhs of a rule
	 * @return true, if the materialization is non-empty
	 */
	public boolean needsMaterialization(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		
		Pair<IndexedNonterminal, IndexedNonterminal> requestPair =
				new Pair<>(materializableNonterminal, instantiableNonterminal);
		
		if( ! knownMatches.containsKey(requestPair) ){
			computeMatch(materializableNonterminal, instantiableNonterminal);
		}
		
		return ! knownMatches.get(requestPair).first().isEmpty();
	}

	/**
	 * The sequence of symbols with which to replace the abstract stack symbol at the end
	 * of the stack of the materializableNonterminal in order to achieve equal stacks
	 * @param materializableNonterminal the nonterminal in the graph which shall be replaced
	 * @param instantiableNonterminal the nonterminal representing the lhs of a rule
	 * @return a list of stack symbols
	 */
	private List<StackSymbol> getNecessaryMaterialization(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		Pair<IndexedNonterminal, IndexedNonterminal> requestPair =
				new Pair<>(materializableNonterminal, instantiableNonterminal);
		
		if( ! knownMatches.containsKey(requestPair) ){
			computeMatch(materializableNonterminal, instantiableNonterminal);
		}
		
		return knownMatches.get(requestPair).first();
	}
	
	public Pair<AbstractStackSymbol, List<StackSymbol> > getMaterializationRule(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		
		if( needsMaterialization( materializableNonterminal, instantiableNonterminal ) ) {
			AbstractStackSymbol lhs = (AbstractStackSymbol) materializableNonterminal.getStack().getLastStackSymbol();
			return new Pair<>( lhs, getNecessaryMaterialization(materializableNonterminal, instantiableNonterminal) );
		}else {
			return new Pair<>( null, new ArrayList<>() );
		}
	}

	/**
	 * Determines whether the materializableNonterminal has to be instantiated to achieve equal stacks
	 * (can be false for concrete stacks)
	 * @param materializableNonterminal the nonterminal in the graph which shall be replaced
	 * @param instantiableNonterminal the nonterminal representing the lhs of a rule
	 * @return true, if the instantiationSequence is non-empty
	 */
	public boolean needsInstantiation(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		Pair<IndexedNonterminal, IndexedNonterminal> requestPair =
				new Pair<>(materializableNonterminal, instantiableNonterminal);
		
		if( ! knownMatches.containsKey(requestPair) ){
			computeMatch(materializableNonterminal, instantiableNonterminal);
		}
		
		return ! knownMatches.get(requestPair).second().isEmpty();
	}

	/**
	  The sequence of symbols with which to replace the stack variable at the end
	 * of the stack of the instantiableNonterminal in order to achieve equal stacks
	 * @param materializableNonterminal the nonterminal in the graph that shall be replaced
	 * @param instantiableNonterminal the nonterminal representing the lhs in the grammar
	 * @return The list of stack symbols 
	 */
	public List<StackSymbol> getNecessaryInstantiation(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal ) {
		Pair<IndexedNonterminal, IndexedNonterminal> requestPair =
				new Pair<>(materializableNonterminal, instantiableNonterminal);
		
		if( ! knownMatches.containsKey(requestPair) ){
			computeMatch(materializableNonterminal, instantiableNonterminal);
		}
		
		return knownMatches.get(requestPair).second();
	}
	
	private void computeMatch(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal ){
		
		Pair<IndexedNonterminal, IndexedNonterminal> requestPair =
				new Pair<>(materializableNonterminal, instantiableNonterminal);

		if( ! materializableNonterminal.getLabel().equals(instantiableNonterminal.getLabel() ) ) {
			addNegativeResultToKnownMatches(requestPair);
			return;
		}
		
		List<StackSymbol> necessaryMaterialization = new ArrayList<>();
		List<StackSymbol> necessaryInstantiation = new ArrayList<>();
		
			
			for( int i = 0; i < materializableNonterminal.getStack().size()
						 || i < instantiableNonterminal.getStack().size();
				i++ ){
				StackSymbol s1 = getNextSymbolForMaterializableNonterminal(materializableNonterminal, necessaryMaterialization, i);
				StackSymbol s2 = getNextSymbolForInstantiableNonterminal(instantiableNonterminal, i);
				
				if( s1 instanceof ConcreteStackSymbol 
						&& s2 instanceof ConcreteStackSymbol
						&& (! s1.equals(s2)) ){
					addNegativeResultToKnownMatches(requestPair);
					return;
				}else if( s2 instanceof StackVariable ){
					necessaryInstantiation.add( s1 );
				}else if( s1 instanceof AbstractStackSymbol && s2 instanceof ConcreteStackSymbol ){
					if( ! necessaryMaterialization.isEmpty() ){
						necessaryMaterialization.remove( necessaryMaterialization.size() -1 );
					}
					if( stackGrammar.canCreateSymbolFor( s1, s2 ) ){
					necessaryMaterialization.addAll( stackGrammar.getRuleCreatingSymbolFor( s1, s2 ) );
					}else{
						addNegativeResultToKnownMatches( requestPair );
						return;
					}
				}
			}

			Pair<List<StackSymbol>, List<StackSymbol>> result = new Pair<>(necessaryMaterialization, necessaryInstantiation);
			knownMatches.put(requestPair, result);
		
	}



	private StackSymbol getNextSymbolForInstantiableNonterminal(IndexedNonterminal instantiableNonterminal, int i) {
		StackSymbol s2;
		if( i < instantiableNonterminal.getStack().size() ){
		    s2 = instantiableNonterminal.getStack().get(i);
		}else{
			s2 = StackVariable.getGlobalInstance();
		}
		return s2;
	}

	private StackSymbol getNextSymbolForMaterializableNonterminal(IndexedNonterminal materializableNonterminal,
			List<StackSymbol> necessaryMaterialization, int i) {
		StackSymbol s1;
		if( i < materializableNonterminal.getStack().size() ){
		 s1 = materializableNonterminal.getStack().get( i );
		}else{
		  /* 
		   * +1 is added because the first symbol in the materialization
		   * replaces the last symbol in the normal stack.
		   * Example NT[s,s,X], materialization [a,b,Y] -> then a replaces X.
		   * The result of applying the materialization would be NT[s,s,a,b,Y] 	
		   */
		  s1 = necessaryMaterialization.get(i - materializableNonterminal.getStack().size() +1 );
		}
		return s1;
	}

	private void addNegativeResultToKnownMatches(Pair<IndexedNonterminal, IndexedNonterminal> requestPair) {
		knownMatches.put( requestPair, null );
	}

}
