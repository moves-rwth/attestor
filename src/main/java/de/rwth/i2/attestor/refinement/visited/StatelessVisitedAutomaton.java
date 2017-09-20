package de.rwth.i2.attestor.refinement.visited;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import gnu.trove.iterator.TIntIterator;

import java.util.Collections;
import java.util.Set;

public class StatelessVisitedAutomaton implements StatelessHeapAutomaton {

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int markedNode = findMarkedNode(heapConfiguration);

        if(heapConfiguration.attachedVariablesOf(markedNode).size() > 1) {
            return Collections.singleton("{ visited }");
        }

        return Collections.emptySet();
    }

    private int findMarkedNode(HeapConfiguration heapConfiguration) {

        TIntIterator varIter = heapConfiguration.variableEdges().iterator();
        while(varIter.hasNext()) {
            int var = varIter.next();
            if(heapConfiguration.nameOf(var).startsWith(Marking.MARKING_PREFIX)) {
                return heapConfiguration.targetOf(var);
            }
        }
        return HeapConfiguration.INVALID_ELEMENT;
    }
}
