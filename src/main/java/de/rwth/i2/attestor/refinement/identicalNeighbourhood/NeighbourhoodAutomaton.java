package de.rwth.i2.attestor.refinement.identicalNeighbourhood;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import gnu.trove.iterator.TIntIterator;

import java.util.Collections;
import java.util.Set;

public class NeighbourhoodAutomaton extends SceneObject implements StatelessHeapAutomaton {

    private final Marking marking;

    public NeighbourhoodAutomaton(SceneObject sceneObject, Marking marking) {

        super(sceneObject);
        this.marking = marking;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int varNode = heapConfiguration.variableTargetOf(marking.getUniversalVariableName());

        TIntIterator iter = heapConfiguration.variableEdges().iterator();
        while (iter.hasNext()) {
            int var = iter.next();
            String varName = heapConfiguration.nameOf(var);
            String selName = marking.extractSelectorName(varName);
            if (selName != null) {
                int node = heapConfiguration.targetOf(var);
                SelectorLabel label = scene().getSelectorLabel(selName);
                if (heapConfiguration.selectorTargetOf(varNode, label) != node) {
                    return Collections.emptySet();
                }
            }
        }

        return Collections.singleton("{ identicNeighbours }");

    }
}
