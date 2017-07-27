package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * Specialized transition relation to check reachability between two variables.
 *
 * @author Christoph
 */
public class VariableReachabilityTransitionRelation extends ReachabilityTransitionRelation {

    /**
     * Name of the source variable.
     */
    private String fromVariableName;

    /**
     * Name of the target variable.
     */
    private String toVariableName;

    /**
     *
     * @param fromVariableName Name of the source variable.
     * @param toVariableName Name of the target variable.
     */
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
