package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cmath on 7/19/17.
 */
public class MockupTransition implements HeapAutomatonTransition {

    private final static Set<HeapAutomatonState> supportedStates = new HashSet<>();

    @Override
    public HeapAutomatonState move(List<HeapAutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {
        assert(heapConfiguration != null);

        int newState = 0;

        for(HeapAutomatonState s : ntAssignment) {
            if(s instanceof MockupState) {
                newState += ((MockupState) s).getState();
            }
        }

        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            newState += heapConfiguration.selectorLabelsOf(node).size();
        }

        if(newState == 0) {
            return new MockupState(0, false);
        } else {
            return new MockupState(1, true);
        }
    }

    @Override
    public Set<HeapAutomatonState> getSupportedStates() {

        if(supportedStates.isEmpty()) {
            supportedStates.add( new MockupState(0, false) );
            supportedStates.add( new MockupState(1, true) );
        }

        return supportedStates;
    }
}
