package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.refinement.product.ProductHeapAutomaton;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import gnu.trove.iterator.TIntIterator;

import java.util.ArrayList;
import java.util.List;

public class AutomatonStateLabelingStrategy implements StateLabelingStrategy {

    private HeapAutomaton heapAutomaton;

    private List<StatelessHeapAutomaton> statelessHeapAutomata;

    public static AutomatonStateLabelingStrategyBuilder builder() {
        return new AutomatonStateLabelingStrategyBuilder();
    }

    public AutomatonStateLabelingStrategy(HeapAutomaton heapAutomaton) {

        this.heapAutomaton = heapAutomaton;
    }

    public AutomatonStateLabelingStrategy(HeapAutomaton heapAutomaton,
                                          List<StatelessHeapAutomaton> statelessHeapAutomata) {
        this.heapAutomaton = heapAutomaton;
        this.statelessHeapAutomata = statelessHeapAutomata;
    }

    private HeapAutomatonState transition(HeapConfiguration heapConfiguration) {

        return heapAutomaton.transition(heapConfiguration, extractStatesOfNonterminals(heapConfiguration));
    }

    private List<HeapAutomatonState> extractStatesOfNonterminals(HeapConfiguration heapConfiguration) {

        List<HeapAutomatonState> result = new ArrayList<>(heapConfiguration.countNonterminalEdges());
        TIntIterator iter = heapConfiguration.nonterminalEdges().iterator();
        while(iter.hasNext()) {
            int edge = iter.next();
            // If this cast fails the whole configuration is broken and we cannot recover from this here
            RefinedNonterminal nt = (RefinedNonterminal) heapConfiguration.labelOf(edge);
            result.add(nt.getState());
        }

        return result;
    }

    @Override
    public void computeAtomicPropositions(ProgramState programState) {

        HeapConfiguration heapConf = programState.getHeap();
        if(heapAutomaton != null) {
            for (String ap : transition(heapConf).toAtomicPropositions()) {
                programState.addAP(ap);
            }
        }
        for(StatelessHeapAutomaton automaton : statelessHeapAutomata) {
            for(String ap : automaton.transition(heapConf)) {
                programState.addAP(ap);
            }
        }
    }
}

class AutomatonStateLabelingStrategyBuilder {

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

        switch (automata.size()) {
            case 0:
                return new AutomatonStateLabelingStrategy(null, statelessHeapAutomata);
            case 1:
                return new AutomatonStateLabelingStrategy(automata.get(0), statelessHeapAutomata);
            default:
                HeapAutomaton[] automataArray = new HeapAutomaton[automata.size()];
                ProductHeapAutomaton productHeapAutomaton = new ProductHeapAutomaton(automata.toArray(automataArray));
                return new AutomatonStateLabelingStrategy(productHeapAutomaton, statelessHeapAutomata);
        }
    }
}

