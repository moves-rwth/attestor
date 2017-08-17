package de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar;

import java.util.*;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.CannotMatchException;
import de.rwth.i2.attestor.grammar.canonicalization.StackEmbeddingResult;
import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.*;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.iterator.TIntIterator;

public class EmbeddingStackChecker {
	private static final Logger logger = LogManager.getLogger( "EmbeddingStackChecker" );


	IndexMatcher stackMatcher;
	IndexMaterializationStrategy stackMaterializer;


	public EmbeddingStackChecker(IndexMatcher matcher, IndexMaterializationStrategy materializer) {
		this.stackMatcher = matcher;
		this.stackMaterializer = materializer;
	}

	public StackEmbeddingResult getStackEmbeddingResult( HeapConfiguration toAbstract, 
			Matching embedding, 
			Nonterminal lhs ) throws CannotMatchException{

		Map<AbstractIndexSymbol, List<IndexSymbol>> materializations = new HashMap<>();
		List<IndexSymbol> instantiation = new ArrayList<>();

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
					Pair<AbstractIndexSymbol, List<IndexSymbol>> materializationRule = 
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

		toAbstract = applyMaterializationsTo( toAbstract, materializations );
		pattern = applyInstantiationTo( pattern, instantiation );
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
				
				if( ! materializable.getIndex().matchStack(instantiable.getIndex() ) ) {
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
			Map<AbstractIndexSymbol, List<IndexSymbol>> materializations) {
		for( Entry<AbstractIndexSymbol, List<IndexSymbol>> rule : materializations.entrySet() ){
			try {
				hc = this.stackMaterializer.getMaterializedCloneWith(hc, rule.getKey(), rule.getValue() );
			} catch (CannotMaterializeException e) {
				logger.error( "materialization after stack matching faild." );
				e.printStackTrace();
			}
		}
		return hc;
	}
	
	private HeapConfiguration applyInstantiationTo(HeapConfiguration pattern, List<IndexSymbol> instantiation) {
		
		IndexSymbol stackVariable = IndexVariable.getGlobalInstance();
		pattern = pattern.clone();
		HeapConfigurationBuilder builder = pattern.builder();
		TIntIterator edgeIter = pattern.nonterminalEdges().iterator();
		while(edgeIter.hasNext()) {
			int indexOfNonterminal = edgeIter.next();
			Nonterminal nonterminal = pattern.labelOf( indexOfNonterminal );
			if( nonterminal instanceof IndexedNonterminal){
				IndexedNonterminal nonterminalToMaterialize = (IndexedNonterminal) nonterminal;
				if( nonterminalToMaterialize.getIndex().getLastStackSymbol().equals( stackVariable ) ) {
					
					Nonterminal nonterminalWithMaterializedStack = 
							applyInstantiationTo(instantiation, nonterminalToMaterialize);
					builder.replaceNonterminal(indexOfNonterminal, nonterminalWithMaterializedStack );
				}

			}
		}
		HeapConfiguration materializedGraph = builder.build();
		return materializedGraph;
	}



	private void updateMaterializations(Map<AbstractIndexSymbol, List<IndexSymbol>> materializations,
			Pair<AbstractIndexSymbol,List<IndexSymbol>> newMaterializationRule) {

		applyNewMaterialiationTo(materializations,  newMaterializationRule);

		if( !materializations.containsKey(newMaterializationRule.first()) ){
			materializations.put(newMaterializationRule.first(), newMaterializationRule.second());
		}

	}
	
	private void updateInstantiation( List<IndexSymbol> instantiation,
			Pair<AbstractIndexSymbol, List<IndexSymbol>> newMaterializationRule ) {
		
		if( !instantiation.isEmpty() ) {
			materializeIn( instantiation, newMaterializationRule.first(), newMaterializationRule.second() );
		}
	}
	
	private void updateInstantiation(List<IndexSymbol> instantiation, List<IndexSymbol> necessaryInstantiation) throws CannotMatchException {
		
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
			Map<AbstractIndexSymbol, List<IndexSymbol>> materializations,
			Pair<AbstractIndexSymbol,List<IndexSymbol>> newMaterialization) 
	{

		for( Entry<AbstractIndexSymbol, List<IndexSymbol>> materializationRule : materializations.entrySet() ){
			List<IndexSymbol> rhs = materializationRule.getValue();
			materializeIn( rhs, newMaterialization.first(), newMaterialization.second() );
		}
	}

	/**
	 * returns the last element in the stack, i.e. c in [a,b,c].
	 * @param stack the stack containing the elements
	 * @return the last symbol of stack
	 */
	private IndexSymbol getLastSymbolOf(List<IndexSymbol> stack) {
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
	private void materializeIn(List<IndexSymbol> stack, IndexSymbol lhs, List<IndexSymbol> rhs) {

		if( getLastSymbolOf(stack).equals(lhs) ){
			stack.remove( stack.size() -1 );
			stack.addAll( rhs );
		}
	}

	private IndexedNonterminal applyCurrentMaterializationTo( 
			Map<AbstractIndexSymbol, List<IndexSymbol>> currentMaterializations,
			IndexedNonterminal materializable ) 
	{

		IndexSymbol lastStackSymbol = materializable.getIndex().getLastStackSymbol();
		if( lastStackSymbol instanceof AbstractIndexSymbol ){
			if( currentMaterializations.containsKey(lastStackSymbol) ){
				return materializable.getWithProlongedStack( currentMaterializations.get(lastStackSymbol) );
			}
		}
		return materializable;
	}

	private IndexedNonterminal applyInstantiationTo(List<IndexSymbol> instantiation, IndexedNonterminal instantiable) {

		IndexSymbol lastSymbol = instantiable.getIndex().getLastStackSymbol();
		if( ! instantiation.isEmpty() && lastSymbol instanceof IndexVariable ) {
			return instantiable.getWithProlongedStack(instantiation);
		}else {
			return instantiable;
		}

	}

}
