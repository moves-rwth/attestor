package de.rwth.i2.attestor.tasks;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.StateAnnotatedSymbol;
import de.rwth.i2.attestor.graph.Nonterminal;

/**
 * A nonterminal symbol that is additionally annotated with a state of a heap automaton.
 *
 * @author Christoph
 */
public class StateAnnotatedNonterminal implements Nonterminal, StateAnnotatedSymbol {

    /**
     * The actual nonterminal symbol.
     */
    private Nonterminal nonterminal;

    /**
     * The state the nonterminal is annotated with.
     */
    private AutomatonState state;

    public StateAnnotatedNonterminal(Nonterminal nonterminal, AutomatonState state) {
       this.nonterminal = nonterminal;
       this.state = state;
    }

    @Override
    public AutomatonState getState() {

        return state;
    }

    @Override
    public StateAnnotatedSymbol withState(AutomatonState state) {

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

        if(nonterminal instanceof StateAnnotatedNonterminal) {
            StateAnnotatedNonterminal sn = (StateAnnotatedNonterminal) nonterminal;
            if(sn.getState().equals(state) && sn.nonterminal.equals(nonterminal)) {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof StateAnnotatedNonterminal) {
           StateAnnotatedNonterminal n = (StateAnnotatedNonterminal) o;
           return n.getState().equals(state) && n.nonterminal.equals(nonterminal);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nonterminal.hashCode();
    }
}
