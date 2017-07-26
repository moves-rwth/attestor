package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.TransitionRelation;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Transition relation realizing the union of two heap automata.
 * The states of the resulting automata consists of all pairs (p,null) and (null,q) where p,q are states
 * belonging to the first and second transition relation of the two automata in the union, respectively.
 *
 * @author Christoph
 */
public class UnionTransitionRelation implements TransitionRelation {

    /**
     * The transition relation of the first automaton in the union.
     */
    private TransitionRelation firstRelation;

    /**
     * The transition relation of the second automaton in the union.
     */
    private TransitionRelation secondRelation;

    /**
     * @param firstRelation The transition relation of the first automaton in the union.
     * @param secondRelation The transition relation of the second automaton in the union.
     */
    public UnionTransitionRelation(TransitionRelation firstRelation, TransitionRelation secondRelation) {

        this.firstRelation = firstRelation;
        this.secondRelation = secondRelation;
    }


    @Override
    public AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        List<AutomatonState> firstAssignment = new ArrayList<>(ntAssignment.size());
        List<AutomatonState> secondAssignment = new ArrayList<>(ntAssignment.size());

        for(AutomatonState state : ntAssignment) {
            if(!(state instanceof UnionAutomatonState)) {
                throw new IllegalArgumentException("Nonterminal assignment contains invalid states.");
            }
            UnionAutomatonState u = (UnionAutomatonState) state;
            firstAssignment.add(u.first());
            secondAssignment.add(u.second());
        }

        AutomatonState first = firstRelation.move(firstAssignment, heapConfiguration);
        AutomatonState second = secondRelation.move(firstAssignment, heapConfiguration);

        return new UnionAutomatonState(first, second);
    }
}
