package de.rwth.i2.attestor.grammar.canonicalization;

import java.util.*;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.materialization.StackMaterializer;
import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.AbstractStackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackVariable;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.iterator.TIntIterator;

public class EmbeddingStackChecker {
	private static final Logger logger = LogManager.getLogger( "EmbeddingStackChecker" );


	StackMatcher stackMatcher;
	StackMaterializer stackMaterializer;


	public EmbeddingStackChecker(StackMatcher matcher, StackMaterializer materializer) {
		this.stackMatcher = matcher;
		this.stackMaterializer = materializer;
	}

	public StackEmbeddingResult getStackEmbeddingResult( HeapConfiguration toAbstract, 
			Matching embedding, 
			Nonterminal lhs ) throws CannotMatchException{

		Map<AbstractStackSymbol, List<StackSymbol>> materializations = new HashMap<>();
		List<StackSymbol> instantiation = new ArrayList<>();

		HeapConfiguration pattern = embedding.pattern();
		TIntIterator iterator =  pattern.nonterminalEdges().iterator();
		while( iterator.hasNext() ){
			int nt = iterator.next();

			Nonterminal patternLabel = pattern.labelOf( nt );
			Nonterminal targetLabel = toAbstract.labelOf( embedding.match( nt ) );
			if( patternLabel instanceof IndexedNonterminal 
					&& targetLabel instanceof IndexedNonterminal ){

				IndexedNonterminal materializable = (IndexedNonterminal) targetLabel;
				materializable = applyCurrentMaterializationTo( materializations, materializable );
				IndexedNonterminal instantiable = (IndexedNonterminal) patternLabel;
				instantiable = applyInstantiationTo( instantiation, instantiable );

				if(! stackMatcher.canMatch( materializable, instantiable ) ){
					throw new CannotMatchException();
				}else{
					Pair<AbstractStackSymbol, List<StackSymbol>> materializationRule = 
							stackMatcher.getMaterializationRule(materializable, instantiable);

					if( stackMatcher.needsMaterialization(materializable, instantiable) ) {
						updateMaterializations( materializations, materializationRule );
						updateInstantiation( instantiation, materializationRule );
					}
					if( stackMatcher.needsInstantiation(materializable, instantiable) ) {
						updateInstantiation( instantiation, stackMatcher.getNecessaryInstantiation(materializable, instantiable) );
					}
				}
			}

		}

		toAbstract = applyMaterializationsTo(toAbstract, materializations);
		if( ! instantiation.isEmpty() && lhs instanceof IndexedNonterminal ) {
			IndexedNonterminal iLhs = (IndexedNonterminal) lhs;
			lhs = iLhs.getWithProlongedStack(instantiation);
		}
		
		checkAppliedResult(toAbstract, embedding, pattern);
		return new StackEmbeddingResult( toAbstract, lhs );
	}

	/**
	 * To avoid checking corner cases in the original compuation of matchings,
	 * the stacks with applied materialization and instantiation are checked for
	 * equality.
	 * @param toAbstract the outer graph
	 * @param embedding the matching from pattern to outer graph elements
	 * @param pattern the embedded graph
	 * @throws CannotMatchException if one of the stacks does not match
	 */
	private void checkAppliedResult(HeapConfiguration toAbstract, Matching embedding, HeapConfiguration pattern)
			throws CannotMatchException {
		
		TIntIterator iterator =  pattern.nonterminalEdges().iterator();
		while( iterator.hasNext() ){
			int nt = iterator.next();

			Nonterminal patternLabel = pattern.labelOf( nt );
			Nonterminal targetLabel = toAbstract.labelOf( embedding.match( nt ) );
			if( patternLabel instanceof IndexedNonterminal 
					&& targetLabel instanceof IndexedNonterminal ){

				IndexedNonterminal materializable = (IndexedNonterminal) targetLabel;
				IndexedNonterminal instantiable = (IndexedNonterminal) patternLabel;
				
				if( ! materializable.matchStack(instantiable) ) {
					throw new CannotMatchException();
				}
			}
		}
	}





	/**
	 * Applies all the materialization rules in materializations to the
	 * graph hc.
	 * @param hc the graph to which to apply the materializations
	 * @param materializations the rules for materialization, e.g. X -> ssX, Y -> sZ
	 * @return
	 */
	private HeapConfiguration applyMaterializationsTo(HeapConfiguration hc,
			Map<AbstractStackSymbol, List<StackSymbol>> materializations) {
		for( Entry<AbstractStackSymbol, List<StackSymbol>> rule : materializations.entrySet() ){
			try {
				hc = this.stackMaterializer.getMaterializedCloneWith(hc, rule.getKey(), rule.getValue() );
			} catch (CannotMaterializeException e) {
				logger.error( "materialization after stack matching faild." );
				e.printStackTrace();
			}
		}
		return hc;
	}


	private void updateMaterializations(Map<AbstractStackSymbol, List<StackSymbol>> materializations,
			Pair<AbstractStackSymbol,List<StackSymbol>> newMaterializationRule) {

		applyNewMaterialiationTo(materializations,  newMaterializationRule);

		if( !materializations.containsKey(newMaterializationRule.first()) ){
			materializations.put(newMaterializationRule.first(), newMaterializationRule.second());
		}

	}
	
	private void updateInstantiation( List<StackSymbol> instantiation,
			Pair<AbstractStackSymbol, List<StackSymbol>> newMaterializationRule ) {
		
		if( !instantiation.isEmpty() ) {
			materializeIn( instantiation, newMaterializationRule.first(), newMaterializationRule.second() );
		}
	}
	
	private void updateInstantiation(List<StackSymbol> instantiation, List<StackSymbol> necessaryInstantiation) throws CannotMatchException {
		
		if( ! instantiation.isEmpty() && ! instantiation.equals( necessaryInstantiation ) ) {
			throw new CannotMatchException();
		}
		else instantiation.addAll( necessaryInstantiation );
	}

	/**
	 * applies the new materialization to all the current materializations.
	 * For example if materializations contains the rules
	 * X -> sX,
	 * Y -> ssX and
	 * A -> ssB
	 * and newMaterializations is (X,sZ),
	 * materializations will afterwards consist of
	 * X -> ssZ,
	 * Y -> sssZ and
	 * > -> ssB. 
	 * @param materializations
	 * @param newMaterialization
	 */
	private void applyNewMaterialiationTo(
			Map<AbstractStackSymbol, List<StackSymbol>> materializations,
			Pair<AbstractStackSymbol,List<StackSymbol>> newMaterialization) 
	{

		for( Entry<AbstractStackSymbol, List<StackSymbol>> materializationRule : materializations.entrySet() ){
			List<StackSymbol> rhs = materializationRule.getValue();
			materializeIn( rhs, newMaterialization.first(), newMaterialization.second() );
		}
	}

	/**
	 * returns the last element in the stack, i.e. c in [a,b,c].
	 * @param stack the stack containing the elements
	 * @return the last symbol of stack
	 */
	private StackSymbol getLastSymbolOf(List<StackSymbol> stack) {
		return stack.get( stack.size() - 1 );
	}

	/**
	 * materializes the given stack using the rule lhs -> rhs.
	 * For example if stack = ssX, lhs = X, rhs = Z stack is afterwards ssZ.
	 * If the last symbol of stack does not equal lhs nothing happens,
	 * so for example calling stack = ssY, lhs = X, rhs = Z will leave stack unchanged.
	 * @param stack the stack to materialize
	 * @param lhs the abstract stack symbol to materialized
	 * @param rhs the sequence of stack symbols with which to materialize
	 */
	private void materializeIn(List<StackSymbol> stack, StackSymbol lhs, List<StackSymbol> rhs) {

		if( getLastSymbolOf(stack).equals(lhs) ){
			stack.remove( stack.size() -1 );
			stack.addAll( rhs );
		}
	}

	private IndexedNonterminal applyCurrentMaterializationTo( 
			Map<AbstractStackSymbol, List<StackSymbol>> currentMaterializations,
			IndexedNonterminal materializable ) 
	{

		StackSymbol lastStackSymbol = materializable.getLastStackSymbol();
		if( lastStackSymbol instanceof AbstractStackSymbol ){
			AbstractStackSymbol abs = (AbstractStackSymbol) lastStackSymbol;
			if( currentMaterializations.containsKey(lastStackSymbol) ){
				return materializable.getWithProlongedStack( currentMaterializations.get(lastStackSymbol) );
			}
		}
		return materializable;
	}

	private IndexedNonterminal applyInstantiationTo(List<StackSymbol> instantiation, IndexedNonterminal instantiable) {

		StackSymbol lastSymbol = instantiable.getLastStackSymbol();
		if( ! instantiation.isEmpty() && lastSymbol instanceof StackVariable ) {
			return instantiable.getWithProlongedStack(instantiation);
		}else {
			return instantiable;
		}

	}

}
