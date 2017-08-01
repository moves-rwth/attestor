package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockupHeapAutomaton extends HeapAutomaton {

    @Override
    protected AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

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
}

/**
 * Simple example of a heap automaton (state) that checks whether selector edges exist.
 *
 * @author Christoph
 */
class MockUpState implements AutomatonState {

    private int state;
    private boolean finalState;

    MockUpState(int state, boolean finalState) {
        this.state = state;
        this.finalState = finalState;
    }

    @Override
    public boolean isFinal() {
        return finalState;
    }

    @Override
    public Set<String> getAtomicPropositions() {

        Set<String> res = new HashSet<>();
        res.add(String.valueOf(state));
        return res;
    }

    public int getState() {
        return state;
    }

    public boolean equals(Object o) {
        if(o instanceof MockUpState) {
            MockUpState s = (MockUpState) o;
            return s.state == state && s.finalState == finalState;
        }
        return false;
    }

    public int hashCode() {
        return state;
    }

    public String toString() {
        return String.valueOf(state);
    }
}
