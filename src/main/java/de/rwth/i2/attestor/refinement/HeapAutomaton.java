package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.List;

public interface HeapAutomaton {

    HeapAutomatonState transition(HeapConfiguration heapConfiguration, List<HeapAutomatonState> statesOfNonterminals);

    boolean isInitialState(HeapAutomatonState heapAutomatonState);

    List<HeapConfiguration> getPossibleHeapRewritings(HeapConfiguration heapConfiguration);
}
