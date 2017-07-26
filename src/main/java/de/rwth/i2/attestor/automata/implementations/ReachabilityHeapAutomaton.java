package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.HeapAutomaton;

public class ReachabilityHeapAutomaton extends HeapAutomaton {

    public ReachabilityHeapAutomaton(int fromExtNode, int toExtNode, int maxExtNodes) {

        super(new ReachabilityTransitionRelation(fromExtNode, toExtNode, maxExtNodes));
    }
}
