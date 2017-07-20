package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.TransitionRelation;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.List;
import java.util.Set;

public class AcyclicityTransitionRelation implements TransitionRelation {

    @Override
    public AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        return null;
    }

    @Override
    public Set<AutomatonState> getSupportedStates() {

        return null;
    }
}
