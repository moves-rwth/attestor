package de.rwth.i2.attestor.indexedGrammars.stack;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.util.DebugMode;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * A StackMaterializationStrategy that uses a fixed right-regular string grammar 
 * given by the following rules:
 * <ul>
 *     <li>X -> sX</li>
 *     <li>X -> Z</li>
 *     <li>Y -> sY</li>
 *     <li>Y -> C</li>
 * </ul>
 *
 * @author Hannah, Christoph
 */
public class DefaultStackMaterialization implements StackMaterializationStrategy {
	
	public static AbstractStackSymbol SYMBOL_X = AbstractStackSymbol.get("X");
	public static AbstractStackSymbol SYMBOL_Y = AbstractStackSymbol.get("Y");
	public static StackSymbol SYMBOL_s = ConcreteStackSymbol.getStackSymbol( "s", false);
	public static StackSymbol SYMBOL_Z = ConcreteStackSymbol.getStackSymbol( "Z", true );
	public static StackSymbol SYMBOL_C = ConcreteStackSymbol.getStackSymbol( "C", true );

	@Override
	public void materializeStacks(HeapConfiguration heapConfiguration, 
								  StackSymbol originalStackSymbol,
                                  StackSymbol desiredStackSymbol) {

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
                                                              StackSymbol desiredStackSymbol,
                                                              StackSymbol originalStackSymbol) {

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
    private static void checkRules(StackSymbol originalStackSymbol, StackSymbol desiredStackSymbol) {

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
    private static void checkConsistency(HeapConfiguration heapConfiguration, StackSymbol stackSymbol) {

        assert(stackSymbol instanceof AbstractStackSymbol);

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        TIntIterator iter = ntEdges.iterator();
        while(iter.hasNext()) {
            int edge = iter.next();
            IndexedNonterminal l = (IndexedNonterminal) heapConfiguration.labelOf(edge);

            assert( !l.getStack().hasConcreteStack() || !l.getStack().stackEndsWith(stackSymbol) );
        }
    }


    @Override
	public IndexedNonterminal materializeStack( IndexedNonterminal nt, StackSymbol s ) {
		assert( ! nt.getStack().hasConcreteStack() );
		nt = nt.getWithShortenedStack().getWithProlongedStack( s );
		if( ! s.isBottom() ){
			nt = nt.getWithProlongedStack( AbstractStackSymbol.get("X") );
		}
		return nt;		
	}

	@Override
	public List<StackSymbol> getRuleCreatingSymbolFor( StackSymbol originalStackSymbol, 
															StackSymbol desiredStackSymbol ) {
		List<StackSymbol> result = new ArrayList<>();
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
	public boolean canCreateSymbolFor(StackSymbol originalStackSymbol, StackSymbol desiredStackSymbol) {
  

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
