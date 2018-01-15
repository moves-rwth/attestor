package de.rwth.i2.attestor.refinement.visited;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import de.rwth.i2.attestor.semantics.util.Constants;
import gnu.trove.iterator.TIntIterator;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class StatelessVisitedByAutomaton implements StatelessHeapAutomaton {


    private final String markingName;

    public StatelessVisitedByAutomaton(String markingName) {

        this.markingName = markingName;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int markedNode = heapConfiguration.variableTargetOf(markingName);

        // This case may occur if the marking is currently not part of the reachable fragment
        // considered by an interprocedural analysis.
        if (markedNode == HeapConfiguration.INVALID_ELEMENT) {
            return Collections.emptySet();
        }

        Set<String> result = new LinkedHashSet<>();
        TIntIterator iter = heapConfiguration.attachedVariablesOf(markedNode).iterator();
        while (iter.hasNext()) {
            int var = iter.next();
            String label = heapConfiguration.nameOf(var);
            if (!Markings.isMarking(label) && !Constants.isConstant(label)) {
                result.add("{ visited(" + label + ") }");
            }
        }
        return result;
    }
}
