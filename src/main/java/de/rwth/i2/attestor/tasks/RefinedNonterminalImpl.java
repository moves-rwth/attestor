package de.rwth.i2.attestor.tasks;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.RefinedNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;

/**
 * A nonterminal symbol that is additionally annotated with a state of a heap automaton.
 *
 * @author Christoph
 */
public class RefinedNonterminalImpl implements RefinedNonterminal {

    /**
     * The actual nonterminal symbol.
     */
    private Nonterminal nonterminal;

    /**
     * The state the nonterminal is annotated with.
     */
    private AutomatonState state;

    public RefinedNonterminalImpl(Nonterminal nonterminal, AutomatonState state) {
       this.nonterminal = nonterminal;
       this.state = state;
    }

    @Override
    public AutomatonState getState() {

        return state;
    }

    @Override
    public RefinedNonterminal withState(AutomatonState state) {

        return new RefinedNonterminalImpl(nonterminal, state);
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
       return "<" + nonterminal  + "," + state + ">";
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof RefinedNonterminalImpl) {
           RefinedNonterminalImpl n = (RefinedNonterminalImpl) o;
           if(!nonterminal.equals(n.nonterminal)) {
               return false;
           }
           if(state == null) {
               if(n.getState() != null)
                   return false;
           } else {
               return n.getState().equals(state);
           }
           return true;
        }
        return false;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        return prime * ((nonterminal == null) ? 0 : nonterminal.hashCode())
                + ((state == null) ? 0 : state.hashCode());
    }
}
