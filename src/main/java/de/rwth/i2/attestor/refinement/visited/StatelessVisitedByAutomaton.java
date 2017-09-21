package de.rwth.i2.attestor.refinement.visited;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.markings.Markings;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.strategies.VariableScopes;
import gnu.trove.iterator.TIntIterator;

import java.util.HashSet;
import java.util.Set;

public class StatelessVisitedByAutomaton implements StatelessHeapAutomaton {


    private Marking marking;

    public StatelessVisitedByAutomaton(Marking marking) {

        this.marking = marking;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int markedNode = heapConfiguration.variableTargetOf(marking.getUniversalVariableName());

        Set<String> result = new HashSet<>();
        TIntIterator iter = heapConfiguration.attachedVariablesOf(markedNode).iterator();
        while(iter.hasNext()) {
            int var = iter.next();
            String label = heapConfiguration.nameOf(var);
            if(!Markings.isMarking(label) && !Constants.isConstant(label)) {
                result.add("{ visitedBy(" + VariableScopes.getName(label) + ") }");
            }
        }
        return result;
    }
}
