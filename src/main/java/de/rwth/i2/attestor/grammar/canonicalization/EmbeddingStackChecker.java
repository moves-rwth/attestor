package de.rwth.i2.attestor.grammar.canonicalization;

import java.util.*;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.canoncalization.StackEmbeddingResult;
import de.rwth.i2.attestor.grammar.materialization.StackMaterializer;
import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.AbstractStackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

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
			Nonterminal lhs ) {

		Map<AbstractStackSymbol, List<StackSymbol>> materializations = new HashMap<>();

		HeapConfiguration pattern = embedding.pattern();
		TIntArrayList nonterminalIndices = pattern.nonterminalEdges();
		TIntIterator iterator =  nonterminalIndices.iterator();
		while( iterator.hasNext() ){
			int nt = iterator.next();

			Nonterminal patternLabel = pattern.labelOf( nt );
			Nonterminal targetLabel = toAbstract.labelOf( embedding.match( nt ) );
			if( patternLabel instanceof IndexedNonterminal 
					&& targetLabel instanceof IndexedNonterminal ){

				IndexedNonterminal materializable = (IndexedNonterminal) targetLabel;
				applyMaterializationTo( materializations, materializable );
				IndexedNonterminal instantiable = (IndexedNonterminal) patternLabel;
				//applyInstantiationTo( instantiation, instantiable );

				if(! stackMatcher.canMatch( materializable, instantiable ) ){
					return new StackEmbeddingResult( false, null, null );
				}else{
					Pair<AbstractStackSymbol, List<StackSymbol>> materializationRule = 
							stackMatcher.getMaterializationRule(materializable, instantiable);

					final AbstractStackSymbol materializedSymbol = materializationRule.first();
					updateMaterializations( 
							materializations,
							materializedSymbol,
							materializationRule.second()
							);
				}
			}

		}
		
		for( Entry<AbstractStackSymbol, List<StackSymbol>> rule : materializations.entrySet() ){
			try {
				toAbstract = this.stackMaterializer.getMaterializedCloneWith(toAbstract, rule.getKey(), rule.getValue() );
			} catch (CannotMaterializeException e) {
				logger.error( "materialization after stack matching faild." );
				e.printStackTrace();
			}
		}

		return new StackEmbeddingResult(true, toAbstract, lhs );
	}


	private void updateMaterializations(Map<AbstractStackSymbol, List<StackSymbol>> materializations,
			AbstractStackSymbol materializedSymbol, List<StackSymbol> newMaterialization) {

		for( Entry<AbstractStackSymbol, List<StackSymbol>> materializationRule : materializations.entrySet() ){
			List<StackSymbol> rhs = materializationRule.getValue();
			StackSymbol lastStackSymbol = rhs.get( rhs.size() - 1 );
			
			if( lastStackSymbol.equals(lastStackSymbol) ){
				rhs.remove( rhs.size() -1 );
				rhs.addAll( newMaterialization );
			}

		}

	}

	private Nonterminal applyMaterializationTo( Map<AbstractStackSymbol, List<StackSymbol>> materializations,
			IndexedNonterminal materializable ) {

		StackSymbol lastStackSymbol = materializable.getLastStackSymbol();
		if( lastStackSymbol instanceof AbstractStackSymbol ){
			AbstractStackSymbol abs = (AbstractStackSymbol) lastStackSymbol;
			if( materializations.containsKey(lastStackSymbol) ){
				return materializable.getWithProlongedStack( materializations.get(lastStackSymbol) );
			}
		}
		return materializable;
	}

}
