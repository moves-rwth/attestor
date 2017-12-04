package de.rwth.i2.attestor.refinement.grammarRefinement;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.RefinedNonterminal;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InitialHeapConfigurationRefinement {

    private final List<HeapConfiguration> refinedInitialHeapConfigurations;
    private HeapConfiguration initialHeapConf;
    private Grammar grammar;
    private HeapAutomaton heapAutomaton;
    private List<List<Nonterminal>> nonterminalsOfEdges;


    public InitialHeapConfigurationRefinement(HeapConfiguration initialHeapConf,
                                              Grammar grammar, HeapAutomaton heapAutomaton) {

        refinedInitialHeapConfigurations = new ArrayList<>();

        if (initialHeapConf.countNonterminalEdges() == 0) {
            refinedInitialHeapConfigurations.add(initialHeapConf);
            return;
        }

        this.initialHeapConf = initialHeapConf;
        this.grammar = grammar;
        this.heapAutomaton = heapAutomaton;

        computePossibleNonterminalsOfEdges();
        computeRefinedInitialHeapConfigurations();
    }

    private void computePossibleNonterminalsOfEdges() {

        Set<Nonterminal> possibleNonterminals = grammar.getAllLeftHandSides();
        nonterminalsOfEdges = new ArrayList<>();

        TIntArrayList ntEdges = initialHeapConf.nonterminalEdges();
        for (int i = 0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            Nonterminal label = initialHeapConf.labelOf(edge);
            List<Nonterminal> allowedNtsOfEdge = new ArrayList<>();
            for (Nonterminal nt : possibleNonterminals) {
                if (label.getLabel().equals(nt.getLabel())) {
                    RefinedNonterminal refinedNonterminal = (RefinedNonterminal) nt;
                    if (heapAutomaton.isInitialState(refinedNonterminal.getState())) {
                        allowedNtsOfEdge.add(nt);
                    }
                }
            }
            nonterminalsOfEdges.add(allowedNtsOfEdge);
        }
    }

    private void computeRefinedInitialHeapConfigurations() {

        AssignmentIterator<Nonterminal> assignmentIterator = new AssignmentIterator<>(nonterminalsOfEdges);
        while (assignmentIterator.hasNext()) {
            List<Nonterminal> ntAssignment = assignmentIterator.next();
            HeapConfiguration copy = initialHeapConf.clone();
            TIntArrayList ntEdges = copy.nonterminalEdges();
            HeapConfigurationBuilder builder = copy.builder();
            for (int i = 0; i < ntEdges.size(); i++) {
                int edge = ntEdges.get(i);
                builder.replaceNonterminal(edge, ntAssignment.get(i));
            }
            refinedInitialHeapConfigurations.add(builder.build());
        }
    }


    public List<HeapConfiguration> getRefinements() {

        return refinedInitialHeapConfigurations;
    }
}
