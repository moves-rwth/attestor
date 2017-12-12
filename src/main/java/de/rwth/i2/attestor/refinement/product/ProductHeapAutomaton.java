package de.rwth.i2.attestor.refinement.product;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;

import java.util.*;

public class ProductHeapAutomaton implements HeapAutomaton {

    private final HeapAutomaton[] automata;

    public ProductHeapAutomaton(HeapAutomaton... automata) {

        this.automata = automata;
    }

    @Override
    public HeapAutomatonState transition(HeapConfiguration heapConfiguration, List<HeapAutomatonState> statesOfNonterminals) {

        List<ProductHeapAutomatonState> productStates = new ArrayList<>(statesOfNonterminals.size());
        statesOfNonterminals.forEach(s -> productStates.add((ProductHeapAutomatonState) s));

        HeapAutomatonState[] nextStates = new HeapAutomatonState[automata.length];
        for (int i = 0; i < automata.length; i++) {
            List<HeapAutomatonState> statesOfAutomaton = new ArrayList<>(statesOfNonterminals.size());
            for (ProductHeapAutomatonState s : productStates) {
                statesOfAutomaton.add(s.get(i));
            }
            nextStates[i] = automata[i].transition(heapConfiguration, statesOfAutomaton);
        }
        return new ProductHeapAutomatonState(nextStates);
    }

    @Override
    public boolean isInitialState(HeapAutomatonState heapAutomatonState) {

        if (heapAutomatonState.getClass() != ProductHeapAutomatonState.class) {
            return false;
        }

        ProductHeapAutomatonState state = (ProductHeapAutomatonState) heapAutomatonState;
        for (int i = 0; i < state.size(); i++) {
            if (!automata[i].isInitialState(state.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<HeapConfiguration> getPossibleHeapRewritings(HeapConfiguration heapConfiguration) {

        return getPossibleHeapRewritingsOf(Collections.singletonList(heapConfiguration), 0);
    }

    private List<HeapConfiguration> getPossibleHeapRewritingsOf(List<HeapConfiguration> heapConfigurations, int i) {

        if (i == automata.length) {
            return heapConfigurations;
        }

        List<HeapConfiguration> result = new ArrayList<>();
        for (HeapConfiguration hc : heapConfigurations) {
            result.addAll(automata[i].getPossibleHeapRewritings(hc));
        }

        return getPossibleHeapRewritingsOf(result, i + 1);
    }
}

class ProductHeapAutomatonState extends HeapAutomatonState {

    private final HeapAutomatonState[] states;

    ProductHeapAutomatonState(HeapAutomatonState... states) {

        this.states = states;
    }

    int size() {

        return states.length;
    }

    HeapAutomatonState get(int i) {

        return states[i];
    }

    @Override
    public Set<String> toAtomicPropositions() {

        Set<String> result = new LinkedHashSet<>();
        for (HeapAutomatonState state : states) {
            result.addAll(state.toAtomicPropositions());
        }
        return result;
    }

    @Override
    public boolean isError() {

        for (HeapAutomatonState state : states) {
            if (state.isError()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object otherObject) {

        if (otherObject == this) {
            return true;
        }

        if (otherObject == null) {
            return false;
        }

        if (otherObject.getClass() != ProductHeapAutomatonState.class) {
            return false;
        }

        ProductHeapAutomatonState other = (ProductHeapAutomatonState) otherObject;
        return Arrays.equals(states, other.states);
    }

    @Override
    public int hashCode() {

        return Arrays.hashCode(states);
    }

    @Override
    public String toString() {

        return Arrays.toString(states);
    }
}
