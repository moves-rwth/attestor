package de.rwth.i2.attestor.refinement.identicalNeighbourhood;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import gnu.trove.iterator.TIntIterator;

import java.util.Collections;
import java.util.Set;

public class NeighbourhoodAutomaton extends SceneObject implements StatelessHeapAutomaton {

    private final String markingName;

    public NeighbourhoodAutomaton(SceneObject sceneObject, String markingName) {

        super(sceneObject);
        this.markingName = markingName;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int varNode = heapConfiguration.variableTargetOf(markingName);

        TIntIterator iter = heapConfiguration.variableEdges().iterator();
        while (iter.hasNext()) {
            int var = iter.next();
            String varName = heapConfiguration.nameOf(var);
            String selName = extractSelectorName(varName);
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

    private String extractSelectorName(String varName) {
        String[] splitted = varName.split(markingName+ Markings.MARKING_SEPARATOR);
        if(splitted.length == 2) {
            return splitted[1];
        }
        return null;
    }
}
