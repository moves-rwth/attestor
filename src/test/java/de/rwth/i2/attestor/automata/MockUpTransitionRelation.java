package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple example of a heap automaton transition relation that checks whether selector edges exist.
 *
 * @author Christoph
 */
public class MockUpTransitionRelation implements TransitionRelation {

    private final static Set<AutomatonState> supportedStates = new HashSet<>();

    @Override
    public AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {
        assert(heapConfiguration != null);

        int newState = 0;

        for(AutomatonState s : ntAssignment) {
            if(s instanceof MockUpState) {
                newState += ((MockUpState) s).getState();
            }
        }

        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            newState += heapConfiguration.selectorLabelsOf(node).size();
        }

        if(newState == 0) {
            return new MockUpState(0, false);
        } else {
            return new MockUpState(1, true);
        }
    }

    @Override
    public Set<AutomatonState> getSupportedStates() {

        if(supportedStates.isEmpty()) {
            supportedStates.add( new MockUpState(0, false) );
            supportedStates.add( new MockUpState(1, true) );
        }

        return supportedStates;
    }
}
