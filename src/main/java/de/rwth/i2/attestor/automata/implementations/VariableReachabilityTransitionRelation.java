package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class VariableReachabilityTransitionRelation extends ReachabilityTransitionRelation {

    private String fromVariableName;
    private String toVariableName;

    VariableReachabilityTransitionRelation(String fromVariableName, String toVariableName) {

        this.fromVariableName = fromVariableName;
        this.toVariableName = toVariableName;
    }


    @Override
    protected boolean isFinalState(HeapConfiguration canonicalHc, ReachabilityHelper helper) {

        int from = canonicalHc.variableWith(fromVariableName);
        int to = canonicalHc.variableWith(toVariableName);
        if(from != HeapConfiguration.INVALID_ELEMENT && to != HeapConfiguration.INVALID_ELEMENT) {
            int fromNode = canonicalHc.targetOf(from);
            int toNode = canonicalHc.targetOf(to);
            return fromNode != HeapConfiguration.INVALID_ELEMENT
                    && toNode != HeapConfiguration.INVALID_ELEMENT
                    && helper.isReachable(fromNode, toNode);
        }

        return false;
    }
}
