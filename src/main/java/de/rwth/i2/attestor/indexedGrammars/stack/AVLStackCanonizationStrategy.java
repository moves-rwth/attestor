package de.rwth.i2.attestor.indexedGrammars.stack;


import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.*;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * A StackCanonizationStrategy that uses a fixed right-regular string grammar given by the following rules:
 * <ul>
 *     <li>X -> sX</li>
 *     <li>X -> Z</li>
 *     <li>Y -> sY</li>
 *     <li>Y -> C</li>
 * </ul>
 * Furthermore, abstraction is specialized towards features of AVL trees if a HeapConfiguration contains
 * selectors "left" and "right".
 *
 * @author Hannah, Christoph
 */
public class AVLStackCanonizationStrategy implements StackCanonizationStrategy {

	/**
	 * abstracts the stacks of all nonterminals in heapConfiguration simultaneously and as far as possible.
	 * Actually alters the nonterminals in heapConfiguration (no clone is performed).
	 * This method assumes that stack abstraction is sound (e.g. it doesn't check whether anything is linked to null)
	 */
	@Override
	public void canonizeStack( HeapConfiguration heapConfiguration ) {

	   if(!isCanonicalizationAllowed(heapConfiguration))  {
	       return;
       }

	    boolean appliedAbstraction;
	    do {
            appliedAbstraction = attemptAbstraction(heapConfiguration, "Z");
            appliedAbstraction |= attemptAbstraction(heapConfiguration, "X");
            appliedAbstraction |= attemptAbstraction(heapConfiguration, "C");
            appliedAbstraction |= attemptAbstraction(heapConfiguration, "Y");
        } while(appliedAbstraction);
	}

    /**
     * Attempt to abstract the given HeapConfiguration by abstracting the given stack rightmost symbol.
     * @param heapConfiguration The HeapConfiguration that should be abstracted.
     * @param stackLabel The rightmost stack symbol that should be part of the abstraction.
     * @return true if and only if abstraction actually has been applied.
     */
	private boolean attemptAbstraction(HeapConfiguration heapConfiguration, String stackLabel) {

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        TIntArrayList applicableEdges = new TIntArrayList(ntEdges.size()) ;

        TIntIterator iter = ntEdges.iterator();
        while(iter.hasNext()) {
            int edge = iter.next();
            IndexedNonterminal edgeLabel = (IndexedNonterminal) heapConfiguration.labelOf(edge);

            if(isAbstractionPossible(edgeLabel, stackLabel)) {
               if(isAbstractionApplicable(edgeLabel, stackLabel)) {
                   applicableEdges.add(edge);
               }
            } else {
                return false;
            }
        }

        return applyAbstraction(heapConfiguration, applicableEdges);
    }

    /**
     * Checks whether at least one abstraction step that abstracts the given stackLabel is possible at all.
     * @param edgeLabel An edge label that should be checked for possible abstractions.
     * @param stackLabel The rightmost symbol that should be abstracted.
     * @return true if and only if abstraction is possible at all.
     */
    private boolean isAbstractionPossible(IndexedNonterminal edgeLabel, String stackLabel) {

	    String endEdge = edgeLabel.getLastStackSymbol().toString();

	    return (
                (stackLabel.equals("Z") && !endEdge.equals("X"))
                || (stackLabel.equals("C") && !endEdge.equals("Y"))
                || (stackLabel.equals("X") && !endEdge.equals("Z") && (!endEdge.equals("X") || edgeLabel.stackSize() > 1))
                || (stackLabel.equals("Y") && !endEdge.equals("C") && (!endEdge.equals("Y") || edgeLabel.stackSize() > 1))
        );
    }

    /**
     * Checks whether abstraction with the given rightmost symbol is applicable to the given Nonterminal.
     * @param edgeLabel The Nonterminal that should be abstracted.
     * @param stackLabel The label of the rightmost StackSymbol that should be abstracted.
     * @return true if and only if a hyperedge labeled with edgeLabel should be abstracted.
     */
    private boolean isAbstractionApplicable(IndexedNonterminal edgeLabel, String stackLabel) {

	    return edgeLabel.getLastStackSymbol().toString().equals(stackLabel)
                && (stackLabel.equals("Z") || stackLabel.equals("C") || edgeLabel.stackSize() > 1);
    }

    /**
     * Applies abstraction to the stacks of the given edges.
     * @param heapConfiguration The HeapConfiguration whose edges should be abstracted.
     * @param applicableEdges A subset of edges of heapConfiguration that are actually abstracted.
     * @return true if and only if the abstraction has been successfully executed.
     */
    private boolean applyAbstraction(HeapConfiguration heapConfiguration, TIntArrayList applicableEdges) {

	    if(applicableEdges.isEmpty()) {
	        return false;
        }

        applicableEdges.forEach(
            edge -> heapConfiguration
                    .builder()
                    .replaceNonterminal(
                            edge,
                            updateNonterminal((IndexedNonterminal) heapConfiguration.labelOf(edge))
                    ) != null // need a boolean return value
        );

        return true;
    }

    /**
     * Creates an IndexedNonterminal with an updated stack according to the abstraction rules
     * inferred from the underlying context-free string grammar.
     * @param originalNonterminal The nonterminal symbol whose stack should be abstracted.
     * @return A new IndexedNonterminal with an abstracted stack.
     */
    private IndexedNonterminal updateNonterminal(IndexedNonterminal originalNonterminal) {

	    String last = originalNonterminal.getLastStackSymbol().toString();

	    if(last.equals("Z")) {
	        return originalNonterminal
                    .getWithShortenedStack() // Z
                    .getWithProlongedStack(AbstractStackSymbol.get("X")); // -> X
        }

        if(last.equals("X")) {
	        return originalNonterminal
                    .getWithShortenedStack() // X
                    .getWithShortenedStack() // s
                    .getWithProlongedStack(AbstractStackSymbol.get("X")); // -> X
        }

        if(last.equals("C")) {
            return originalNonterminal
                    .getWithShortenedStack() // C
                    .getWithProlongedStack(AbstractStackSymbol.get("Y")); // -> Y
        }

        if(last.equals("Y")) {
            return originalNonterminal
                    .getWithShortenedStack() // Y
                    .getWithShortenedStack() // s
                    .getWithProlongedStack(AbstractStackSymbol.get("Y")); // -> Y
        }

        throw new IllegalStateException("Unknown stack symbol.");
    }


    /**
     * Specialized check for AVL trees that prevents abstractions if
     * selector edges labeled "left" or "right" to null exist.
     * @param heapConfiguration The HeapConfiguration to which canonicalization should be applied.
     * @return true if and only if no selector edges labeled "left" or "right" to null exist.
     */
	private boolean isCanonicalizationAllowed(HeapConfiguration heapConfiguration){
		try{
			
			int varNull = heapConfiguration.variableWith("null");
			int nullNode = heapConfiguration.targetOf(varNull);
			
			TIntIterator iter = heapConfiguration.predecessorNodesOf(nullNode).iterator();
			while(iter.hasNext()) {
				int node = iter.next();
				
				for(SelectorLabel sel : heapConfiguration.selectorLabelsOf(node)) {
					
					if(sel.hasLabel("left") || sel.hasLabel("right")) {
						if(heapConfiguration.selectorTargetOf(node, sel) == nullNode) {
							return false;
						}
					}
				}
			}
			return true;
		}catch( NullPointerException | IllegalArgumentException e ){
			return true;
		}
	}
}
