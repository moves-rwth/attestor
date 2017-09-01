package de.rwth.i2.attestor.refinement.grammarRefinement;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.HeapAutomatonState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.RefinedDefaultNonterminal;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

public class GrammarRefinement {

    private Set<Nonterminal> oldLeftHandSides;
    private Map<Nonterminal, Set<HeapConfiguration>> oldRightHandSides;

    private HeapAutomaton heapAutomaton;

    private Map<Nonterminal, List<HeapAutomatonState>> foundStates = new HashMap<>();

    private Map<Nonterminal, Set<HeapConfiguration>> refinedRules;

    private boolean newRulesFound;

    public GrammarRefinement(Grammar grammar, HeapAutomaton heapAutomaton) {

        this.oldLeftHandSides = grammar.getAllLeftHandSides();
        determineRewrittenOriginalRightHandSides(grammar);
        this.heapAutomaton = heapAutomaton;

        do {
            newRulesFound = false;
            refineAllRules();
        } while(newRulesFound);
    }

    private void determineRewrittenOriginalRightHandSides(Grammar grammar) {

        for(Nonterminal lhs : oldLeftHandSides) {
            Set<HeapConfiguration> rewrittenRhs = new HashSet<>();
            for(HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                rewrittenRhs.addAll(heapAutomaton.getPossibleHeapRewritings(rhs));
            }
            oldRightHandSides.put(lhs, rewrittenRhs);
        }
    }

    public Grammar getRefinedGrammar() {
        return new Grammar(refinedRules);
    }

    private void refineAllRules() {

        for(Nonterminal lhs : oldLeftHandSides) {
            for(HeapConfiguration rhs : oldRightHandSides.get(lhs)) {
                List<List<HeapAutomatonState>> possibleStates = possibleStateAssignments(rhs);
                AssignmentIterator<HeapAutomatonState> iterator = new AssignmentIterator<>(possibleStates);
                while(iterator.hasNext()) {
                    List<HeapAutomatonState> assignment = iterator.next();
                    refineRuleAccordingToAssignment(lhs, rhs, assignment);
                }
            }
        }
    }

    private List<List<HeapAutomatonState>> possibleStateAssignments(HeapConfiguration rhs) {

        List<List<HeapAutomatonState>> result = new ArrayList<>();
        TIntIterator iter = rhs.nonterminalEdges().iterator();
        while(iter.hasNext()) {
            int edge = iter.next();
            Nonterminal nt = rhs.labelOf(edge);
            result.add(foundStates.getOrDefault(nt, Collections.emptyList()));
        }
        return result;
    }

    private void refineRuleAccordingToAssignment(Nonterminal lhs, HeapConfiguration rhs,
                                                 List<HeapAutomatonState> assignment) {

        HeapAutomatonState assignedState = heapAutomaton.transition(rhs, assignment);
        foundStates.getOrDefault(lhs, new ArrayList<>()).add(assignedState);

        Nonterminal refinedLhs = new RefinedDefaultNonterminal(lhs, assignedState);
        HeapConfiguration refinedRhs = refineRightSide(rhs, assignment);

        Set<HeapConfiguration> allRefinedRhsOfLhs = refinedRules.getOrDefault(refinedLhs, new HashSet<>());
        newRulesFound |= allRefinedRhsOfLhs.add(refinedRhs);
    }

    private HeapConfiguration refineRightSide(HeapConfiguration rhs, List<HeapAutomatonState> assignment) {

        rhs = rhs.clone();
        HeapConfigurationBuilder builder = rhs.builder();
        TIntArrayList ntEdges = rhs.nonterminalEdges();
        for(int i=0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            HeapAutomatonState assignedState = assignment.get(i);
            Nonterminal nt = rhs.labelOf(edge);
            Nonterminal refinedNt = new RefinedDefaultNonterminal(nt, assignedState);
            builder.replaceNonterminal(edge, refinedNt);
        }
        return builder.build();
    }
}
