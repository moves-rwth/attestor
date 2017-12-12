package de.rwth.i2.attestor.programState.defaultState;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import de.rwth.i2.attestor.refinement.RefinedNonterminal;

import java.util.Objects;

/**
 * A nonterminal symbol that is additionally annotated with a state of a heap automaton.
 *
 * @author Christoph
 */
public class RefinedDefaultNonterminal implements RefinedNonterminal {

    /**
     * The actual nonterminal symbol.
     */
    private final Nonterminal nonterminal;

    /**
     * The state the nonterminal is annotated with.
     */
    private final HeapAutomatonState state;

    public RefinedDefaultNonterminal(Nonterminal nonterminal, HeapAutomatonState state) {

        this.nonterminal = nonterminal;
        this.state = state;
    }

    @Override
    public HeapAutomatonState getState() {

        return state;
    }

    @Override
    public RefinedNonterminal withState(HeapAutomatonState state) {

        return new RefinedDefaultNonterminal(nonterminal, state);
    }

    @Override
    public int getRank() {

        return nonterminal.getRank();
    }

    @Override
    public String getLabel() {

        return nonterminal.getLabel();
    }

    @Override
    public boolean isReductionTentacle(int tentacle) {

        return nonterminal.isReductionTentacle(tentacle);
    }

    @Override
    public void setReductionTentacle(int tentacle) {

        nonterminal.setReductionTentacle(tentacle);
    }

    @Override
    public void unsetReductionTentacle(int tentacle) {

        nonterminal.unsetReductionTentacle(tentacle);
    }

    @Override
    public String toString() {

        return "<" + nonterminal + "," + state + ">";
    }

    @Override
    public boolean equals(Object otherObject) {

        if (otherObject == this) {
            return true;
        }

        if (otherObject == null) {
            return false;
        }

        if (otherObject.getClass() != RefinedDefaultNonterminal.class) {
            return false;
        }

        RefinedDefaultNonterminal other = (RefinedDefaultNonterminal) otherObject;
        return nonterminal.equals(other.nonterminal)
                && state.equals(other.state);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nonterminal, state);
    }
}
