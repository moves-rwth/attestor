package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.TransitionRelation;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * The (finite) set of all states supported by this transition relation.
     */
    private Set<AutomatonState> supportedStates;

    /**
     * @param firstRelation The transition relation of the first automaton in the union.
     * @param secondRelation The transition relation of the second automaton in the union.
     */
    public UnionTransitionRelation(TransitionRelation firstRelation, TransitionRelation secondRelation) {

        this.firstRelation = firstRelation;
        this.secondRelation = secondRelation;

        supportedStates = new HashSet<>();
        for(AutomatonState first : firstRelation.getSupportedStates()) {
            supportedStates.add(new UnionAutomatonState(first, null));
        }
        for(AutomatonState second : secondRelation.getSupportedStates()) {
            supportedStates.add(new UnionAutomatonState(null, second));
        }
    }


    @Override
    public AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        List<AutomatonState> assignment = new ArrayList<>(ntAssignment.size());
        int relationToUse = 0; // -1 = first, 1 = second, 0 = unknown

        for(AutomatonState state : ntAssignment) {
            if(state instanceof UnionAutomatonState) {
                UnionAutomatonState unionState = (UnionAutomatonState) state;
                AutomatonState first = unionState.first();
                AutomatonState second = unionState.second();
                if(relationToUse != 1 && first != null && second == null) {
                    relationToUse = -1;
                    assignment.add(first);
                } else if (relationToUse != -1 && first == null && second != null) {
                    relationToUse = 1;
                    assignment.add(second);
                } else {
                    throw new IllegalArgumentException("Nonterminal assignment contains invalid states.");
                }
            } else {
                throw new IllegalArgumentException("Nonterminal assignment contains invalid states.");
            }
        }

        switch(relationToUse) {
            case -1:
                return firstRelation.move(assignment, heapConfiguration);
            case 1:
                return secondRelation.move(assignment, heapConfiguration);
            default:
                throw new IllegalArgumentException("Nonterminal assignment contains invalid states.");
        }
    }

    @Override
    public Set<AutomatonState> getSupportedStates() {

        return supportedStates;
    }
}
