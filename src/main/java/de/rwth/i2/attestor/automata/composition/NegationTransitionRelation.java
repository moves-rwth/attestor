package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.TransitionRelation;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.List;

/**
 * Transition relation of a heap automaton that realizes the negation of another
 * <b>deterministic</b> heap automaton.
 *
 * @author Christoph
 */
public class NegationTransitionRelation implements TransitionRelation {

    private TransitionRelation originalTransitionRelation;

    public NegationTransitionRelation(TransitionRelation originalTransitionRelation) {

        this.originalTransitionRelation = originalTransitionRelation;
    }

    @Override
    public AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration) {

        return originalTransitionRelation.move(ntAssignment, heapConfiguration);
    }
}
