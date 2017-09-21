package de.rwth.i2.attestor.refinement.identicalNeighbourhood;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;

import java.util.Collections;
import java.util.Set;

public class NeighbourhoodAutomaton implements StatelessHeapAutomaton {

    private Marking marking;

    public NeighbourhoodAutomaton(Marking marking) {

        this.marking = marking;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int varNode = heapConfiguration.variableTargetOf(marking.getUniversalVariableName());

        if(marking.isMarkAllSuccessors()) {

            for(SelectorLabel sel : heapConfiguration.selectorLabelsOf(varNode)) {

                String varName = marking.getSelectorVariableName(sel.getLabel());
                int varTarget = heapConfiguration.variableTargetOf(varName);
                if(varTarget != HeapConfiguration.INVALID_ELEMENT
                        || varTarget != heapConfiguration.selectorTargetOf(varNode, sel)) {
                    return Collections.emptySet();
                }
            }
        } else {

            for(SelectorLabel sel : marking.getRequiredSelectors()) {

                String varName = marking.getSelectorVariableName(sel.getLabel());
                int varTarget = heapConfiguration.variableTargetOf(varName);
                if(varTarget != HeapConfiguration.INVALID_ELEMENT
                        || varTarget != heapConfiguration.selectorTargetOf(varNode, sel)) {
                    return Collections.emptySet();
                }
            }
        }

        return Collections.singleton("{ identicNeighbourhood }");

    }
}
