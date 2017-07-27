package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.HeapAutomaton;

public class ReachabilityHeapAutomaton extends HeapAutomaton {

    public ReachabilityHeapAutomaton(String fromVariableName, String toVariableName) {

        super(new VariableReachabilityTransitionRelation(fromVariableName, toVariableName));
    }

    public ReachabilityHeapAutomaton(int fromExtNode, int toExtNode) {

        super(new ExternalReachabilityTransitionRelation(fromExtNode, toExtNode));
    }
}
