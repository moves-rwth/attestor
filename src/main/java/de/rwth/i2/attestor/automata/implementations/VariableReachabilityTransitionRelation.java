package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class VariableReachabilityTransitionRelation extends ReachabilityTransitionRelation {

    private String fromVariableName;
    private String toVariableName;

    public VariableReachabilityTransitionRelation(String fromVariableName, String toVariableName) {

        this.fromVariableName = fromVariableName;
        this.toVariableName = toVariableName;
    }


    @Override
    protected boolean isFinalState(HeapConfiguration canonicalHc, ReachabilityHelper helper) {

        int fromNode = canonicalHc.variableWith(fromVariableName);
        int toNode = canonicalHc.variableWith(toVariableName);

        return fromNode != HeapConfiguration.INVALID_ELEMENT
                && toNode != HeapConfiguration.INVALID_ELEMENT
                && helper.isReachable(fromNode, toNode);
    }
}
