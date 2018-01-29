package de.rwth.i2.attestor.refinement.grammarRefinement;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.programState.defaultState.RefinedDefaultNonterminal;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

public class GrammarRefinement {

    private final Set<Nonterminal> oldLeftHandSides;
    private final Map<Nonterminal, Set<HeapConfiguration>> oldRightHandSides = new LinkedHashMap<>();

    private final HeapAutomaton heapAutomaton;

    private final Map<Nonterminal, List<HeapAutomatonState>> foundStates = new LinkedHashMap<>();

    private final Map<Nonterminal, Set<HeapConfiguration>> refinedRules = new LinkedHashMap<>();

    private boolean newRulesFound;

    public GrammarRefinement(Grammar grammar, HeapAutomaton heapAutomaton) {

        this.oldLeftHandSides = grammar.getAllLeftHandSides();
        this.heapAutomaton = heapAutomaton;
        determineRewrittenOriginalRightHandSides(grammar);

        refineBaseRules();
        do {
            newRulesFound = false;
            refineAllRules();
        } while (newRulesFound);
    }

    private void determineRewrittenOriginalRightHandSides(Grammar grammar) {

        for (Nonterminal lhs : oldLeftHandSides) {
            Set<HeapConfiguration> rewrittenRhs = new LinkedHashSet<>();
            for (HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                List<HeapConfiguration> rewritings = heapAutomaton.getPossibleHeapRewritings(rhs);
                rewrittenRhs.addAll(rewritings);
            }
            oldRightHandSides.put(lhs, rewrittenRhs);
        }
    }

    public Grammar getRefinedGrammar() {

        return Grammar.builder().addRules(refinedRules).build();
    }

    private void refineBaseRules() {

        for (Nonterminal lhs : oldLeftHandSides) {
            for (HeapConfiguration rhs : oldRightHandSides.get(lhs)) {
                if (rhs.countNonterminalEdges() == 0) {
                    refineRuleAccordingToAssignment(lhs, rhs, Collections.emptyList());
                }
            }
        }
    }

    private void refineAllRules() {

        for (Nonterminal lhs : oldLeftHandSides) {
            for (HeapConfiguration rhs : oldRightHandSides.get(lhs)) {
                if (rhs.countNonterminalEdges() > 0) {
                    List<List<HeapAutomatonState>> possibleStates = possibleStateAssignments(rhs);
                    AssignmentIterator<HeapAutomatonState> iterator = new AssignmentIterator<>(possibleStates);
                    while (iterator.hasNext()) {
                        List<HeapAutomatonState> assignment = iterator.next();
                        refineRuleAccordingToAssignment(lhs, rhs, assignment);
                    }
                }
            }
        }
    }

    private List<List<HeapAutomatonState>> possibleStateAssignments(HeapConfiguration rhs) {

        List<List<HeapAutomatonState>> result = new ArrayList<>();
        TIntIterator iter = rhs.nonterminalEdges().iterator();
        while (iter.hasNext()) {
            int edge = iter.next();
            Nonterminal nt = rhs.labelOf(edge);
            result.add(foundStates.getOrDefault(nt, Collections.emptyList()));
        }
        return result;
    }

    private void refineRuleAccordingToAssignment(Nonterminal lhs, HeapConfiguration rhs,
                                                 List<HeapAutomatonState> assignment) {

        HeapAutomatonState assignedState = heapAutomaton.transition(rhs, assignment);

        if (assignedState.isError()) {
            return;
        }

        foundStates.putIfAbsent(lhs, new ArrayList<>());
        List<HeapAutomatonState> states = foundStates.get(lhs);
        if (!states.contains(assignedState)) {
            states.add(assignedState);
        }

        Nonterminal refinedLhs = new RefinedDefaultNonterminal(lhs, assignedState);
        HeapConfiguration refinedRhs = refineRightSide(rhs, assignment);

        if (!refinedRules.containsKey(refinedLhs)) {
            newRulesFound = true;
            refinedRules.put(refinedLhs, new LinkedHashSet<>());
        }
        Set<HeapConfiguration> allRefinedRhsOfLhs = refinedRules.get(refinedLhs);
        allRefinedRhsOfLhs.add(refinedRhs);
    }

    private HeapConfiguration refineRightSide(HeapConfiguration rhs, List<HeapAutomatonState> assignment) {

        rhs = rhs.clone();
        HeapConfigurationBuilder builder = rhs.builder();
        TIntArrayList ntEdges = rhs.nonterminalEdges();
        for (int i = 0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            HeapAutomatonState assignedState = assignment.get(i);
            Nonterminal nt = rhs.labelOf(edge);
            Nonterminal refinedNt = new RefinedDefaultNonterminal(nt, assignedState);
            builder.replaceNonterminal(edge, refinedNt);
        }
        return builder.build();
    }
}
