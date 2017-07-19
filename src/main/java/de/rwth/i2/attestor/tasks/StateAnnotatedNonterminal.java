package de.rwth.i2.attestor.tasks;

import de.rwth.i2.attestor.automata.HeapAutomaton;
import de.rwth.i2.attestor.automata.HeapAutomatonState;
import de.rwth.i2.attestor.automata.StateAnnotatedSymbol;
import de.rwth.i2.attestor.graph.Nonterminal;

/**
 * Created by cmath on 7/19/17.
 */
public class StateAnnotatedNonterminal implements Nonterminal, StateAnnotatedSymbol {

    private Nonterminal nonterminal;
    private HeapAutomatonState state;

    public StateAnnotatedNonterminal(Nonterminal nonterminal, HeapAutomatonState state) {
       this.nonterminal = nonterminal;
       this.state = state;
    }

    @Override
    public HeapAutomatonState getState() {

        return state;
    }

    @Override
    public StateAnnotatedSymbol withState(HeapAutomatonState state) {

        return new StateAnnotatedNonterminal(nonterminal, state);
    }

    @Override
    public int getRank() {

        return nonterminal.getRank();
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
    public boolean labelMatches(Nonterminal nonterminal) {

        return nonterminal.labelMatches(nonterminal);
    }

    @Override
    public int compareTo(Nonterminal nonterminal) {
        // TODO this might not be correct
        return nonterminal.compareTo(nonterminal);
    }
}
