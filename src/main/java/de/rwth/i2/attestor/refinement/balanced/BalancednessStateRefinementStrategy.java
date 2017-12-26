package de.rwth.i2.attestor.refinement.balanced;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

public class BalancednessStateRefinementStrategy extends SceneObject implements StateRefinementStrategy {

    private BalancednessHelper helper;

    public BalancednessStateRefinementStrategy(SceneObject sceneObject) {

        super(sceneObject);
        SelectorLabel left = sceneObject.scene().getSelectorLabel("left");
        SelectorLabel right = sceneObject.scene().getSelectorLabel("right");
        helper = new BalancednessHelper(this, left, right);
    }


    @Override
    public ProgramState refine(SemanticsCommand semanticsCommand, ProgramState state) {

        helper.updateSelectorAnnotations(state.getHeap());
        return state;
    }
}


class BalancednessHelper extends SceneObject {

    private SelectorLabel left;
    private SelectorLabel right;

    BalancednessHelper(SceneObject sceneObject, SelectorLabel left, SelectorLabel right) {

        super(sceneObject);
        this.left = left;
        this.right = right;
    }

    void updateSelectorAnnotations(HeapConfiguration heapConfiguration) {

        Map<Integer, Integer> heights = new LinkedHashMap<>();
        Set<Integer> visited = new LinkedHashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();

        int nullVariable = heapConfiguration.variableWith(Constants.NULL);

        if (nullVariable == HeapConfiguration.INVALID_ELEMENT) {
            return;
        }

        int nullNode = heapConfiguration.targetOf(nullVariable);

        if (nullNode == HeapConfiguration.INVALID_ELEMENT) {
            return;
        }

        heights.put(nullNode, -1);

        initializeLeaves(heapConfiguration, heights, visited, queue, nullNode);
        initializeNodesWithNts(heapConfiguration, heights, visited, queue);

        while (!queue.isEmpty()) {
            int v = queue.remove();
            if (tryComputeHeightAndAdjustAnnotations(v, heapConfiguration, heights)) {
                visited.add(v);
                addParentToQueue(heapConfiguration, v, queue, visited);
            }
        }
    }

    private void initializeNodesWithNts(HeapConfiguration hc,
                                        Map<Integer, Integer> heights,
                                        Set<Integer> visited,
                                        Queue<Integer> queue) {

        Nonterminal btLabel = scene().getNonterminal("BT");

        TIntArrayList ntEdges = hc.nonterminalEdges();


        TIntIterator iter = ntEdges.iterator();
        while (iter.hasNext()) {

            int ntEdge = iter.next();
            if (!(hc.labelOf(ntEdge) instanceof IndexedNonterminal)) {
                continue;
            }
            IndexedNonterminal nt = (IndexedNonterminal) hc.labelOf(ntEdge);
            if (nt.getLabel().equals(btLabel.getLabel())) {

                int sourceNode = hc.attachedNodesOf(ntEdge).get(4);
                //assume indices of form s*Z if something is linked to null and s*X otherwise
                heights.put(sourceNode, nt.getIndex().size() - 1);

                addParentToQueue(hc, sourceNode, queue, visited);
                visited.add(sourceNode);
            }
        }
    }

    private void initializeLeaves(HeapConfiguration hc,
                                  Map<Integer, Integer> heights,
                                  Set<Integer> visited,
                                  Queue<Integer> queue,
                                  int nullNode) {

        TIntIterator iter = hc.predecessorNodesOf(nullNode).iterator();
        while (iter.hasNext()) {
            int leaf = iter.next();

            if (tryComputeHeightAndAdjustAnnotations(leaf, hc, heights)) {

                visited.add(leaf);
                addParentToQueue(hc, leaf, queue, visited);
            }
        }
    }

    private boolean tryComputeHeightAndAdjustAnnotations
            (int node,
             HeapConfiguration hc,
             Map<Integer, Integer> heights
            ) {

        boolean hasLeft = false, hasRight = false;
        int leftNode = -1, rightNode = -1;
        AnnotatedSelectorLabel leftLabel = null, rightLabel = null;

        for (SelectorLabel nodeSel : hc.selectorLabelsOf(node)) {

            AnnotatedSelectorLabel sel = (AnnotatedSelectorLabel) nodeSel;

            if (sel.hasLabel("left")) {
                leftLabel = sel;
                leftNode = hc.selectorTargetOf(node, leftLabel);
                hasLeft = heights.containsKey(leftNode);
            } else if (sel.hasLabel("right")) {
                rightLabel = sel;
                rightNode = hc.selectorTargetOf(node, rightLabel);
                hasRight = heights.containsKey(rightNode);
            }
        }
        if (hasLeft && hasRight) {
            int leftHeight = heights.get(leftNode);
            int rightHeight = heights.get(rightNode);
            int diff = leftHeight - rightHeight;
            heights.put(node, Math.max(heights.get(leftNode), heights.get(rightNode)) + 1);

            adjustAnnotations(node, hc, leftLabel, rightLabel, diff);

            return true;
        }

        return false;
    }

    private void adjustAnnotations(int node,
                                   HeapConfiguration hc, AnnotatedSelectorLabel leftLabel,
                                   AnnotatedSelectorLabel rightLabel, int diff) {


        AnnotatedSelectorLabel newLeft = new AnnotatedSelectorLabel(left, "" + diff);
        AnnotatedSelectorLabel newRight = new AnnotatedSelectorLabel(right, "" + (-diff));
        hc.builder().replaceSelector(node, leftLabel, newLeft)
                .replaceSelector(node, rightLabel, newRight)
                .build();
    }

    private void addParentToQueue(HeapConfiguration hc, int node, Queue<Integer> queue, Set<Integer> visited) {

        for (SelectorLabel sel : hc.selectorLabelsOf(node)) {
            if (sel.hasLabel("parent")) {
                int parentNode = hc.selectorTargetOf(node, sel);
                if (!visited.contains(parentNode)) {
                    queue.add(parentNode);
                    return;
                }
            }
        }
    }

}
