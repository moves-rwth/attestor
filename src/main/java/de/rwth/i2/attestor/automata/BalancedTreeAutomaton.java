package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

public class BalancedTreeAutomaton extends HeapAutomaton {


    @Override
    protected AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        BalancednessHelper.updateSelectorAnnotations(heapConfiguration);
        TIntArrayList potentialRoots = determineRoots(heapConfiguration);
        return new BalancedTreeState( isBalancedTree(heapConfiguration, potentialRoots) );
    }

    private TIntArrayList determineRoots(HeapConfiguration heapConfiguration) {

        TIntArrayList result = new TIntArrayList();
        int nullNode = getNullNode(heapConfiguration);
        if(nullNode == HeapConfiguration.INVALID_ELEMENT) {
            return result;
        }

        TIntIterator rootsIter = heapConfiguration.predecessorNodesOf(nullNode).iterator();
        while(rootsIter.hasNext()) {
            int potentialRoot = rootsIter.next();
            List<SelectorLabel> selectorLabels = heapConfiguration.selectorLabelsOf(potentialRoot);
            for(SelectorLabel sel : selectorLabels) {
                if(sel.hasLabel("parent") && heapConfiguration.selectorTargetOf(potentialRoot, sel) == nullNode) {
                    result.add(potentialRoot);
                }
            }
        }

        return result;
    }

    private int getNullNode(HeapConfiguration heapConfiguration)  {

        int nullVariable = heapConfiguration.variableWith("null");
        if(nullVariable == HeapConfiguration.INVALID_ELEMENT) {
            return nullVariable;
        }
        return heapConfiguration.targetOf(nullVariable);
    }

    private boolean isBalancedTree(HeapConfiguration heapConfiguration, TIntArrayList potentialRoots) {

        if(potentialRoots.size() != 1) {
            return false;
        }

        int root = potentialRoots.get(0);
        boolean foundLeft = false;
        boolean foundRight = false;

        List<SelectorLabel> selectorLabels = heapConfiguration.selectorLabelsOf(root);
        for(SelectorLabel sel : selectorLabels) {
            if (sel.hasLabel("left")) {
                String annotation = ((AnnotatedSelectorLabel) sel).getAnnotation();
                foundLeft = annotation.equals("-1") || annotation.equals("0") || annotation.equals("1");
            } else if (sel.hasLabel("right")) {
                String annotation = ((AnnotatedSelectorLabel) sel).getAnnotation();
                foundRight = annotation.equals("-1") || annotation.equals("0") || annotation.equals("1");
            }
        }

        return foundLeft && foundRight;
    }
}

class BalancedTreeState implements AutomatonState {

    private static final String AP_BALANCED = "balanced";

    private final boolean isFinal;

    BalancedTreeState(boolean isFinal) {

        this.isFinal = isFinal;
    }

    @Override
    public boolean isFinal() {

        return this.isFinal;
    }

    @Override
    public Set<String> getAtomicPropositions() {

        Set<String> result = new HashSet<>();
        if(isFinal) {
            result.add(AP_BALANCED);
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof BalancedTreeState;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "BTS";
    }


}

/**
 * Auxiliary class annotate selectors pointing to children of a binary tree with balancedness information.
 *
 * @author Christoph
 */
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

    @Override
    public boolean equals(Object obj) {

        return obj instanceof BalancedTreeState;
    }

    @Override
    public int hashCode() {

        return 0;
    }

}
