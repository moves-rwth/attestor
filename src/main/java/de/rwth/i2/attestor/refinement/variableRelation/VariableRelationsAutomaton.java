package de.rwth.i2.attestor.refinement.variableRelation;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import de.rwth.i2.attestor.strategies.VariableScopes;
import gnu.trove.iterator.TIntIterator;

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

        int lhsNode = HeapConfiguration.INVALID_ELEMENT;
        int rhsNode = HeapConfiguration.INVALID_ELEMENT;

        TIntIterator varIter = heapConfiguration.variableEdges().iterator();
        while(varIter.hasNext()
                && lhsNode == HeapConfiguration.INVALID_ELEMENT && rhsNode == HeapConfiguration.INVALID_ELEMENT) {
            int var = varIter.next();

            // remove scoping information first
            String name = VariableScopes.getName(heapConfiguration.nameOf(var));

            if(name.equals(lhs)) {
                lhsNode = heapConfiguration.targetOf(var);
            }

            if(name.equals(rhs)) {
                rhsNode = heapConfiguration.targetOf(var);
            }
        }

        if(lhsNode == HeapConfiguration.INVALID_ELEMENT || rhsNode == HeapConfiguration.INVALID_ELEMENT) {
            return Collections.emptySet();
        }

        if(lhsNode == rhsNode) {
            return equalityAPs;
        }

        return inequalityAPs;
    }
}
