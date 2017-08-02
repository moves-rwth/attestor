package de.rwth.i2.attestor.tasks.indexedTask;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.RefinedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminalImpl;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;

import java.util.List;

public class RefinedIndexedNonterminal extends IndexedNonterminalImpl implements RefinedNonterminal {

    private AutomatonState state;

    public RefinedIndexedNonterminal(String label, int rank, boolean[] isReductionTentacle,
                                     List<StackSymbol> stack, AutomatonState state) {
        super(label, rank, isReductionTentacle, stack);
        this.state = state;
    }

    public RefinedIndexedNonterminal(String label, List<StackSymbol> stack, AutomatonState state) {
        super(label, stack);
        this.state = state;
    }

    protected RefinedIndexedNonterminal(RefinedIndexedNonterminal nonterminal, AutomatonState state) {
        super(nonterminal.generalNonterminal, nonterminal.stack);
        this.state = state;
    }

    @Override
    public AutomatonState getState() {
        return state;
    }

    @Override
    public RefinedNonterminal withState(AutomatonState state) {
        return new RefinedIndexedNonterminal(this, state);
    }

    @Override
    public boolean equals(Object other) {

        if(other instanceof RefinedIndexedNonterminal) {

            RefinedIndexedNonterminal nt = (RefinedIndexedNonterminal) other;
            return super.equals(nt) && state.equals(nt.state);
        }
        return false;
    }

    @Override
    public int hashCode() {

        return super.hashCode() + 37 * state.hashCode();
    }

}
