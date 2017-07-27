package de.rwth.i2.attestor.automata.implementations.balancedness;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.TransitionRelation;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.AnnotatedSelectorLabel;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;

public class BalancedTreeTransitionRelation implements TransitionRelation {

    @Override
    public AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        BalancednessHelper.updateSelectorAnnotations(heapConfiguration);
        TIntArrayList potentialRoots = determineRoots(heapConfiguration);
        return new BalancedTreeAutomatonState( isBalancedTree(heapConfiguration, potentialRoots) );
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
        int nullNode = heapConfiguration.targetOf(nullVariable);
        return nullNode;
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
