package de.rwth.i2.attestor.grammar.materialization;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.grammar.materialization.communication.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;

/**
 * Capable of handling {@link MaterializationAndRuleResponse}
 * in addition to all {@link GrammarResponse}s handled by {@link DefaultGrammarResponseApplier}
 * 
 * @author Hannah
 *
 */
public class IndexedGrammarResponseApplier extends DefaultGrammarResponseApplier {

	private static final Logger logger = LogManager.getLogger( "IndexedGrammarResponseApplier" );
	
	StackMaterializer stackMaterializer;

	public IndexedGrammarResponseApplier(StackMaterializer stackMaterializer,
			GraphMaterializer graphMaterializer) {
		super( graphMaterializer );
		this.stackMaterializer = stackMaterializer;
	}
	
	public Collection<HeapConfiguration> applyGrammarResponseTo( HeapConfiguration inputGraph, 
			int edgeId, 
			GrammarResponse grammarResponse) 
			throws WrongResponseTypeException {
		
		if( grammarResponse instanceof MaterializationAndRuleResponse ){
			 MaterializationAndRuleResponse indexedRespose = 
					 (MaterializationAndRuleResponse) grammarResponse;
			
			 Collection<HeapConfiguration> materializedGraphs = new ArrayList<>();
			 
			 for( List<StackSymbol> materialization : indexedRespose.getPossibleMaterializations() ){
				 	 
				 HeapConfiguration materializedStacks;
				try {
					materializedStacks = stackMaterializer
							.getMaterializedCloneWith(inputGraph, 
													  indexedRespose.getStackSymbolToMaterialize(), 
													  materialization );
					 materializedGraphs.addAll( super.applyGrammarResponseTo(materializedStacks, 
							 edgeId, 
							 new DefaultGrammarResponse( indexedRespose
									 .getRulesForMaterialization(materialization))) ); 
				} catch (CannotMaterializeException e) {
					logger.error("Could not materialize a graph. graph: " + inputGraph );
				}
				
			 }
			 
			 return materializedGraphs;
		}else{
			return super.applyGrammarResponseTo( inputGraph, edgeId, grammarResponse );
		}
	}

}

