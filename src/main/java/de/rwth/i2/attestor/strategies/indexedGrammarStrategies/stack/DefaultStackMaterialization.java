package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.util.DebugMode;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * A StackMaterializationStrategy that uses a fixed right-regular string grammar 
 * given by the following rules:
 * <ul>
 *     <li>X &#8594; sX</li>
 *     <li>X &#8594; Z</li>
 *     <li>Y &#8594; sY</li>
 *     <li>Y &#8594; C</li>
 * </ul>
 *
 * @author Hannah, Christoph
 */
public class DefaultStackMaterialization implements StackMaterializationStrategy {
	
	public static AbstractIndexSymbol SYMBOL_X = AbstractIndexSymbol.get("X");
	public static AbstractIndexSymbol SYMBOL_Y = AbstractIndexSymbol.get("Y");
	public static IndexSymbol SYMBOL_s = ConcreteIndexSymbol.getStackSymbol( "s", false);
	public static IndexSymbol SYMBOL_Z = ConcreteIndexSymbol.getStackSymbol( "Z", true );
	public static IndexSymbol SYMBOL_C = ConcreteIndexSymbol.getStackSymbol( "C", true );

	@Override
	public void materializeStacks(HeapConfiguration heapConfiguration, 
								  IndexSymbol originalStackSymbol,
                                  IndexSymbol desiredStackSymbol) {

		if( DebugMode.ENABLED ) {
            checkRules(originalStackSymbol, desiredStackSymbol);
            checkConsistency(heapConfiguration, originalStackSymbol);
		}

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
		TIntIterator iter = ntEdges.iterator();
		while(iter.hasNext()) {
			int edge = iter.next();
			IndexedNonterminal indexedNonterminal = (IndexedNonterminal) heapConfiguration.labelOf(edge);
			if(indexedNonterminal.getStack().stackEndsWith(originalStackSymbol)) {

			    IndexedNonterminal updatedNt = getNonterminalWithUpdatedStack(indexedNonterminal,
                        desiredStackSymbol, originalStackSymbol);

                heapConfiguration.builder()
                        .replaceNonterminal(
                                edge,
                                updatedNt
                        )
                        .build();
            }
		}
	}

    /**
     * Materializes an IndexedNonterminal by adding the desired stack symbol.
     * @param nonterminal The nonterminal whose stack should be materialized.
     * @param desiredStackSymbol The StackSymbol that should be added through materialization.
     * @param originalStackSymbol The StackSymbol that is materialized.
     * @return A new IndexedNonterminal with an updated stack.
     */
    private IndexedNonterminal getNonterminalWithUpdatedStack(IndexedNonterminal nonterminal,
                                                              IndexSymbol desiredStackSymbol,
                                                              IndexSymbol originalStackSymbol) {

        IndexedNonterminal result = nonterminal.getWithShortenedStack().
        											getWithProlongedStack(desiredStackSymbol);
        if(!desiredStackSymbol.isBottom()) {
            result = result.getWithProlongedStack(originalStackSymbol);
        }
        return  result;
    }

    /**
     * Checks whether the originalStackSymbol may be materialized to get the desiredStackSymbol
     * according to the rules of the grammar.
     * @param originalStackSymbol The original (abstract) StackSymbol that should be replaced.
     * @param desiredStackSymbol The desired StackSymbol that should be obtained through 
     * materialization.
     */
    private static void checkRules(IndexSymbol originalStackSymbol, IndexSymbol desiredStackSymbol) {

        String original = originalStackSymbol.toString();
        String desired = desiredStackSymbol.toString();

        assert(
                (original.equals("X") && (desired.equals("s") || desired.equals("Z")))
                        || (original.equals("Y") && (desired.equals("s") || desired.equals("C")))
                );
    }

    /**
     * Performs a sanity check to ensure that stack materialization to replace 
     * the given StackSymbol is actually applicable to the given HeapConfiguration.
     * @param heapConfiguration The HeapConfiguration that should be checked.
     * @param stackSymbol The stackSymbol that should be materialized.
     */
    private static void checkConsistency(HeapConfiguration heapConfiguration, IndexSymbol stackSymbol) {

        assert(stackSymbol instanceof AbstractIndexSymbol);

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        TIntIterator iter = ntEdges.iterator();
        while(iter.hasNext()) {
            int edge = iter.next();
            IndexedNonterminal l = (IndexedNonterminal) heapConfiguration.labelOf(edge);

            assert( !l.getStack().hasConcreteStack() || !l.getStack().stackEndsWith(stackSymbol) );
        }
    }


    @Override
	public IndexedNonterminal materializeStack( IndexedNonterminal nt, IndexSymbol s ) {
		assert( ! nt.getStack().hasConcreteStack() );
		nt = nt.getWithShortenedStack().getWithProlongedStack( s );
		if( ! s.isBottom() ){
			nt = nt.getWithProlongedStack( AbstractIndexSymbol.get("X") );
		}
		return nt;		
	}

	@Override
	public List<IndexSymbol> getRuleCreatingSymbolFor( IndexSymbol originalStackSymbol, 
															IndexSymbol desiredStackSymbol ) {
		List<IndexSymbol> result = new ArrayList<>();
		if(  originalStackSymbol.equals(SYMBOL_X) ){
			if( desiredStackSymbol.equals(SYMBOL_s) ){
				result.add(SYMBOL_s);
				result.add(SYMBOL_X);
			}else if( desiredStackSymbol.equals(SYMBOL_Z) ){
				result.add(SYMBOL_Z);
			}
		}else if( originalStackSymbol.equals(SYMBOL_Y) ){
			if( desiredStackSymbol.equals(SYMBOL_s) ){
				result.add( SYMBOL_s );
				result.add( SYMBOL_Y );
			}else if( desiredStackSymbol.equals(SYMBOL_C) ){
				result.add(SYMBOL_C);
			}
		}
		return result;
	}

	@Override
	public boolean canCreateSymbolFor(IndexSymbol originalStackSymbol, IndexSymbol desiredStackSymbol) {
  

        return (
                	(originalStackSymbol.equals(SYMBOL_X) 
                	&& (	   desiredStackSymbol.equals(SYMBOL_s) 
                				|| desiredStackSymbol.equals(SYMBOL_Z))
                )
               || 
               		(originalStackSymbol.equals(SYMBOL_Y) 
               		&& (		desiredStackSymbol.equals(SYMBOL_s) 
               					|| desiredStackSymbol.equals(SYMBOL_C )
               					)
               		)
                );
	}


}
