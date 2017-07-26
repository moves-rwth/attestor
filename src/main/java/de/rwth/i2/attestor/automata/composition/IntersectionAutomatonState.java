package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.AutomatonState;

import java.util.HashSet;
import java.util.Set;

/**
 * State of a heap automaton realizing the intersection of two heap automata.
 *
 * @author Christoph
 */
public class IntersectionAutomatonState implements AutomatonState {

    private AutomatonState firstState;
    private AutomatonState secondState;
    private Set<String> atomicPropositions;

    public IntersectionAutomatonState(AutomatonState firstState, AutomatonState secondState) {

        this.firstState = firstState;
        this.secondState = secondState;

        atomicPropositions = new HashSet<>(firstState.getAtomicPropositions());
        atomicPropositions.retainAll(secondState.getAtomicPropositions());
    }

    public AutomatonState first() {

        return firstState;
    }

    public AutomatonState second() {

        return secondState;
    }

    @Override
    public boolean isFinal() {

        return firstState.isFinal() && secondState.isFinal();
    }

    @Override
    public Set<String> getAtomicPropositions() {

        return atomicPropositions;
    }

    @Override
    public Set<String> getAllAtomicPropositions() {

        Set<String> res = new HashSet<>(firstState.getAllAtomicPropositions());
        res.addAll(secondState.getAllAtomicPropositions());
        return res;
    }
}
