package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.refinement.product.ProductHeapAutomaton;

import java.util.ArrayList;
import java.util.List;

public class AutomatonStateLabelingStrategyBuilder {

    List<HeapAutomaton> automata = new ArrayList<>();
    List<StatelessHeapAutomaton> statelessHeapAutomata = new ArrayList<>();

    public AutomatonStateLabelingStrategyBuilder add(HeapAutomaton automaton) {
        automata.add(automaton);
        return this;
    }

    public AutomatonStateLabelingStrategyBuilder add(StatelessHeapAutomaton automaton) {
        statelessHeapAutomata.add(automaton);
        return this;
    }

    public AutomatonStateLabelingStrategy build() {

        if(automata.isEmpty() && statelessHeapAutomata.isEmpty()) {
            return null;
        }

        return new AutomatonStateLabelingStrategy(getProductAutomaton(), statelessHeapAutomata);
    }

    public HeapAutomaton getProductAutomaton() {

        switch (automata.size()) {
            case 0:
                return null;
            case 1:
                return automata.get(0);
            default:
                HeapAutomaton[] automataArray = new HeapAutomaton[automata.size()];
                return new ProductHeapAutomaton(automata.toArray(automataArray));
        }
    }
}

