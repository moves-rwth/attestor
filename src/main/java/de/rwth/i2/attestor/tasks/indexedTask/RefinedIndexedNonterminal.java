package de.rwth.i2.attestor.tasks.indexedTask;

import de.rwth.i2.attestor.automata.AutomatonState;
import de.rwth.i2.attestor.automata.RefinedNonterminal;
import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminalImpl;
import de.rwth.i2.attestor.indexedGrammars.stack.Stack;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;

import java.util.ArrayList;
import java.util.List;

public class RefinedIndexedNonterminal implements RefinedNonterminal, IndexedNonterminal {

    private IndexedNonterminal indexedNonterminal;
    private AutomatonState state;

    public RefinedIndexedNonterminal(IndexedNonterminal indexedNonterminal, AutomatonState state) {
        this.indexedNonterminal = indexedNonterminal;
        this.state = state;
    }

    public RefinedIndexedNonterminal(String label, int rank, boolean[] isReductionTentacle,
                                     List<StackSymbol> stack, AutomatonState state) {

        this.indexedNonterminal = new IndexedNonterminalImpl(label, rank, isReductionTentacle, stack);
        this.state = state;
    }

    public RefinedIndexedNonterminal(String label, int rank, boolean[] isReductionTentacle) {
        this.indexedNonterminal = new IndexedNonterminalImpl(label, rank, isReductionTentacle, new ArrayList<>());
        this.state = null;
    }

    public RefinedIndexedNonterminal(String label, List<StackSymbol> stack, AutomatonState state) {
        this.indexedNonterminal = new IndexedNonterminalImpl(label, stack);
        this.state = state;
    }

    @Override
    public AutomatonState getState() {
        return state;
    }

    @Override
    public RefinedNonterminal withState(AutomatonState state) {
        return new RefinedIndexedNonterminal(indexedNonterminal, state);
    }

    @Override
    public Stack getStack() {
        return indexedNonterminal.getStack();
    }

    @Override
    public IndexedNonterminal getWithShortenedStack() {
        return new RefinedIndexedNonterminal(indexedNonterminal.getWithShortenedStack(), state);
    }

    @Override
    public IndexedNonterminal getWithProlongedStack(StackSymbol s) {
        return new RefinedIndexedNonterminal(indexedNonterminal.getWithProlongedStack(s), state);
    }

    @Override
    public IndexedNonterminal getWithInstantiation() {
        return new RefinedIndexedNonterminal(indexedNonterminal.getWithInstantiation(), state);
    }

    @Override
    public IndexedNonterminal getWithProlongedStack(List<StackSymbol> postfix) {
        return new RefinedIndexedNonterminal(indexedNonterminal.getWithProlongedStack(postfix), state);
    }

    @Override
    public IndexedNonterminal getWithStack(List<StackSymbol> stack) {
        return new RefinedIndexedNonterminal(indexedNonterminal.getWithStack(stack), state);
    }

    @Override
    public int getRank() {
        return indexedNonterminal.getRank();
    }

    @Override
    public boolean isReductionTentacle(int tentacle) {
        return indexedNonterminal.isReductionTentacle(tentacle);
    }

    @Override
    public void setReductionTentacle(int tentacle) {
        indexedNonterminal.setReductionTentacle(tentacle);
    }

    @Override
    public void unsetReductionTentacle(int tentacle) {
        indexedNonterminal.unsetReductionTentacle(tentacle);
    }

    @Override
    public String getLabel() {
        return indexedNonterminal.getLabel();
    }

    @Override
    public boolean matches( NodeLabel obj ){
        return indexedNonterminal.matches(obj);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RefinedIndexedNonterminal other = (RefinedIndexedNonterminal) obj;
            return indexedNonterminal.equals(other.indexedNonterminal)
                    && ((state == null && other.state == null)
                    || (state != null && state.equals(other.state)));
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        return prime * (state == null ? 0 : state.hashCode()) + indexedNonterminal.hashCode();
    }

    @Override
    public String toString() {
        return "(" + indexedNonterminal.toString() + ", "
                + ((state == null) ? "_" : state.toString()) + ")";
    }
}
