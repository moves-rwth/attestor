package de.rwth.i2.attestor.refinement.visited;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import gnu.trove.iterator.TIntIterator;

import java.util.HashSet;
import java.util.Set;

public class StatelessVisitedByAutomaton implements StatelessHeapAutomaton {

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int markedNode = findMarkedNode(heapConfiguration);

        Set<String> result = new HashSet<>();
        TIntIterator iter = heapConfiguration.attachedVariablesOf(markedNode).iterator();
        while(iter.hasNext()) {
            int var = iter.next();
            String label = heapConfiguration.nameOf(var);
            if(!label.startsWith(Marking.MARKING_PREFIX)) {
                result.add("{ visitedBy(" + label + ") }");
            }
        }
        return result;
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
