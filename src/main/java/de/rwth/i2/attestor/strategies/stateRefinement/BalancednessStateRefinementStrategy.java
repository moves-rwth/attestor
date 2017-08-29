package de.rwth.i2.attestor.strategies.stateRefinement;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

public class BalancednessStateRefinementStrategy implements StateRefinementStrategy {

    @Override
    public ProgramState refine(ProgramState state) {

        BalancednessHelper.updateSelectorAnnotations(state.getHeap());
        return state;
    }
}


class BalancednessHelper {

    static void updateSelectorAnnotations(HeapConfiguration heapConfiguration) {

        Map<Integer, Integer> heights = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();

        int nullVariable = heapConfiguration.variableWith("null");

        if(nullVariable == HeapConfiguration.INVALID_ELEMENT) {
            return;
        }

        int nullNode = heapConfiguration.targetOf(nullVariable);

        if(nullNode == HeapConfiguration.INVALID_ELEMENT) {
            return;
        }

        heights.put( nullNode, -1 );

        initializeLeaves( heapConfiguration, heights, visited, queue, nullNode );
        initializeNodesWithNts( heapConfiguration, heights, visited, queue );

        while( ! queue.isEmpty() ){
            int v = queue.remove();
            if( tryComputeHeightAndAdjustAnnotations( v, heapConfiguration, heights ) ){
                visited.add( v );
                addParentToQueue(heapConfiguration, v, queue, visited );
            }
        }
    }

    private static void initializeNodesWithNts( HeapConfiguration hc,
                                                Map<Integer, Integer> heights,
                                                Set<Integer> visited,
                                                Queue<Integer> queue ) {

        Nonterminal btLabel = BasicNonterminal.getNonterminal("BT");

        TIntArrayList ntEdges = hc.nonterminalEdges();


        TIntIterator iter = ntEdges.iterator();
        while(iter.hasNext()) {

            int ntEdge = iter.next();
            IndexedNonterminal nt = (IndexedNonterminal) hc.labelOf(ntEdge);
            if(nt.getLabel().equals(btLabel.getLabel())) {

                int sourceNode = hc.attachedNodesOf(ntEdge).get(4);
                //assume indices of form s*Z if something is linked to null and s*X otherwise
                heights.put(sourceNode, nt.getIndex().size() -1 );

                addParentToQueue(hc, sourceNode, queue, visited );
                visited.add( sourceNode );
            }
        }
    }

    private static void initializeLeaves( HeapConfiguration hc,
                                          Map<Integer, Integer> heights,
                                          Set<Integer> visited,
                                          Queue<Integer> queue,
                                          int nullNode ) {

        TIntIterator iter = hc.predecessorNodesOf(nullNode).iterator();
        while(iter.hasNext()) {
            int leaf = iter.next();

            if(tryComputeHeightAndAdjustAnnotations(leaf, hc, heights)) {

                visited.add( leaf );
                addParentToQueue(hc, leaf, queue, visited);
            }
        }
    }

    private static boolean tryComputeHeightAndAdjustAnnotations
            ( int node,
              HeapConfiguration hc,
              Map<Integer, Integer> heights
            ){
        boolean hasLeft=false, hasRight=false;
        int leftNode = -1, rightNode = -1;
        AnnotatedSelectorLabel leftLabel = null, rightLabel= null;

        for(SelectorLabel nodeSel : hc.selectorLabelsOf(node)) {

            AnnotatedSelectorLabel sel = (AnnotatedSelectorLabel) nodeSel;

            if( sel.hasLabel( "left" ) ){
                leftLabel = sel;
                leftNode = hc.selectorTargetOf(node, leftLabel);
                hasLeft = heights.containsKey( leftNode );
            }else if( sel.hasLabel( "right" ) ){
                rightLabel = sel;
                rightNode = hc.selectorTargetOf(node, rightLabel);
                hasRight = heights.containsKey( rightNode );
            }
        }
        if( hasLeft && hasRight ){
            int leftHeight = heights.get( leftNode );
            int rightHeight = heights.get( rightNode );
            int diff = leftHeight - rightHeight;
            heights.put( node, Math.max( heights.get( leftNode ), heights.get( rightNode ) ) +1 );

            adjustAnnotations( node, hc, leftLabel, rightLabel, diff );

            return true;
        }

        return false;
    }

    private static void adjustAnnotations( int node,
                                           HeapConfiguration hc, AnnotatedSelectorLabel leftLabel,
                                           AnnotatedSelectorLabel rightLabel, int diff ) {
        AnnotatedSelectorLabel newLeft = new AnnotatedSelectorLabel( "left", "" +  diff );
        AnnotatedSelectorLabel newRight = new AnnotatedSelectorLabel( "right", ""+ (-diff) );
        hc.builder().replaceSelector( node, leftLabel, newLeft )
                .replaceSelector( node, rightLabel, newRight )
                .build();
    }

    private static void addParentToQueue(HeapConfiguration hc, int node, Queue<Integer> queue, Set<Integer> visited){

        for(SelectorLabel sel : hc.selectorLabelsOf(node)) {
            if(sel.hasLabel("parent")) {
                int parentNode = hc.selectorTargetOf(node, sel);
                if( !visited.contains( parentNode ) ){
                    queue.add( parentNode );
                    return;
                }
            }
        }
    }

}
