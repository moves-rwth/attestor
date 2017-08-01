package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class IntersectionAutomaton extends HeapAutomaton {

    private HeapAutomaton lhs;
    private HeapAutomaton rhs;

    public IntersectionAutomaton(HeapAutomaton lhs, HeapAutomaton rhs)  {

        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    protected AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        List<AutomatonState> firstAssignment = new ArrayList<>(ntAssignment.size());
        List<AutomatonState> secondAssignment = new ArrayList<>(ntAssignment.size());
        for(AutomatonState state : ntAssignment) {
            if(state instanceof IntersectionState) {
                IntersectionState iState = (IntersectionState) state;
                firstAssignment.add(iState.leftState);
                secondAssignment.add(iState.rightState);
            } else {
                throw new IllegalArgumentException("Invalid state assigned to nonterminal hyperedge.");
            }
        }
        return new IntersectionState(
                lhs.move(firstAssignment, heapConfiguration),
                rhs.move(secondAssignment, heapConfiguration)
        );

    }
}


class IntersectionState implements AutomatonState {

    AutomatonState leftState;
    AutomatonState rightState;

    public IntersectionState(AutomatonState leftState, AutomatonState rightState) {

        this.leftState = leftState;
        this.rightState = rightState;
    }

    @Override
    public boolean isFinal() {

        return leftState.isFinal() && rightState.isFinal();
    }

    @Override
    public Set<String> getAtomicPropositions() {

        Set<String> result = leftState.getAtomicPropositions();
        result.addAll(rightState.getAtomicPropositions());
        return result;
    }
}
