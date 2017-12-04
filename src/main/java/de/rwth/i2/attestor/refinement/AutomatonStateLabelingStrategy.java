package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import gnu.trove.iterator.TIntIterator;

import java.util.ArrayList;
import java.util.List;

public class AutomatonStateLabelingStrategy implements StateLabelingStrategy {

    private final HeapAutomaton heapAutomaton;

    private List<StatelessHeapAutomaton> statelessHeapAutomata;

    public AutomatonStateLabelingStrategy(HeapAutomaton heapAutomaton) {

        this.heapAutomaton = heapAutomaton;
    }

    public AutomatonStateLabelingStrategy(HeapAutomaton heapAutomaton,
                                          List<StatelessHeapAutomaton> statelessHeapAutomata) {

        this.heapAutomaton = heapAutomaton;
        this.statelessHeapAutomata = statelessHeapAutomata;
    }

    public static AutomatonStateLabelingStrategyBuilder builder() {

        return new AutomatonStateLabelingStrategyBuilder();
    }

    private HeapAutomatonState transition(HeapConfiguration heapConfiguration) {

        return heapAutomaton.transition(heapConfiguration, extractStatesOfNonterminals(heapConfiguration));
    }

    private List<HeapAutomatonState> extractStatesOfNonterminals(HeapConfiguration heapConfiguration) {

        List<HeapAutomatonState> result = new ArrayList<>(heapConfiguration.countNonterminalEdges());
        TIntIterator iter = heapConfiguration.nonterminalEdges().iterator();
        while (iter.hasNext()) {
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
        if (heapAutomaton != null) {
            for (String ap : transition(heapConf).toAtomicPropositions()) {
                programState.addAP(ap);
            }
        }
        for (StatelessHeapAutomaton automaton : statelessHeapAutomata) {
            for (String ap : automaton.transition(heapConf)) {
                programState.addAP(ap);
            }
        }
    }
}

