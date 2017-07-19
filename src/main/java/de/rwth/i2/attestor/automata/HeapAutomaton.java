package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.tasks.StateAnnotatedNonterminal;
import gnu.trove.iterator.TIntIterator;

import java.util.*;

/**
 * Created by cmath on 7/19/17.
 */
public class HeapAutomaton {

    private HeapAutomatonTransition transitions;

    public HeapAutomaton(HeapAutomatonTransition transitions) {
        this.transitions = transitions;
    }

    /**
     * Run the automaton on a heap configuration.
     * @param heapConfiguration A heap configuration whose nonterminal edges are already annotated with states of the
     *                          automaton.
     * @return The state reached after one transition step.
     */
    public HeapAutomatonState move(HeapConfiguration heapConfiguration) {

        return transitions.move(
                extractStateAssignment(heapConfiguration),
                heapConfiguration
        );
    }

    /**
     * Determines a list of all automaton states assigned to nonterminal edges in the given heap configuration.
     * The order of this list coincides with the order of nonterminal edges.
     * @param heapConfiguration A heap configuration whose automaton states should be determined.
     * @return A list of the automaton states assigned to each nonterminal edge.
     */
    private List<HeapAutomatonState> extractStateAssignment(HeapConfiguration heapConfiguration) {

        List<HeapAutomatonState> stateAssignments = new ArrayList<>(heapConfiguration.countNonterminalEdges());
        TIntIterator iter = heapConfiguration.nonterminalEdges().iterator();
        while(iter.hasNext()) {
            int edge = iter.next();
            Nonterminal nt = heapConfiguration.labelOf(edge);
            stateAssignments.add( extractState(nt) );
        }
        return stateAssignments;
    }

    /**
     * Determines the automaton state assigned to a given nonterminal.
     * If no such state exists an IllegalStateException is thrown.
     * @param nt The nonterminal.
     * @return The automaton state corresponding to the nonterminal.
     */
    private HeapAutomatonState extractState(Nonterminal nt) {
        if(nt instanceof StateAnnotatedSymbol) {
            HeapAutomatonState res = ((StateAnnotatedSymbol) nt).getState();
            if(res != null) {
                return res;
            }
        }

        throw new IllegalStateException("Provided nonterminal is not annotated with an automaton state.");
    }

    public Grammar refine(Grammar grammar) {

        List<StateAnnotatedNonterminal> annotatedNonterminals = new ArrayList<>();
        Map<Nonterminal, Set<HeapConfiguration>> rules = new HashMap<>();

        for(Nonterminal nonterminal : grammar.getAllLeftHandSides()) {


        }

        return grammar;
    }
}
