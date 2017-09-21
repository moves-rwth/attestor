package de.rwth.i2.attestor.refinement.variableRelation;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;

import java.util.Collections;
import java.util.Set;

public class VariableRelationsAutomaton implements StatelessHeapAutomaton {

    private final String lhs;
    private final String rhs;

    private final Set<String> equalityAPs;
    private final Set<String> inequalityAPs;

    public VariableRelationsAutomaton(String lhs, String rhs) {

        this.lhs = lhs;
        this.rhs = rhs;

        equalityAPs = Collections.singleton("{ " + lhs + " == " + rhs + " }");
        inequalityAPs = Collections.singleton("{ " + lhs + " != " + rhs + " }");
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        // TODO this might collide with scoping...
        int lhsNode = heapConfiguration.variableTargetOf(lhs);
        int rhsNode = heapConfiguration.variableTargetOf(rhs);

        if(lhsNode == rhsNode) {
            return equalityAPs;
        } else {
            return inequalityAPs;
        }
    }
}
