package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.tasks.StateAnnotatedNonterminal;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

/**
 * An auxiliary class that performs the actual refinement of a graph grammar according to a heap automaton.
 *
 * @author Christoph
 */
class GrammarRefinementHelper {

    /**
     * The grammar that should be refined.
     */
    private Grammar grammar;

    /**
     * The transition relation of the heap automaton used for refinement.
     */
    private HeapAutomatonTransition transitions;

    /**
     * This flag is set to false as long as no new rules have been added to the refined grammar within
     * a single iteration. Thus, if the flag is false after a full iteration, we have reached a fixpoint
     * and may successfully terminate the refinement procedure.
     */
    private boolean hasAddedNewRules;

    /**
     * Stores all pairs of nonterminal symbols and assigned heap automaton states as a mapping from
     * nonterminals to a list of states (in the order in which states have been detected).
     */
    private Map<Nonterminal, List<HeapAutomatonState>> foundStates;

    /**
     * The rules of the refined grammar constructed so far.
     */
    private Map<Nonterminal, Set<HeapConfiguration>> refinedGrammarRules;

    /**
     * Refines a given grammar according to the transition relation of a heap automaton.
     * @param grammar The grammar that should be refined.
     * @param transitions The transition relation of a heap automaton guiding the refinement.
     */
    public GrammarRefinementHelper(Grammar grammar, HeapAutomatonTransition transitions) {

        this.grammar = grammar;
        this.transitions = transitions;
        foundStates = new HashMap<>();
        refinedGrammarRules = new HashMap<>();
        refineGrammar();
    }

    /**
     * @return The refined grammar.
     */
    public Grammar getRefinedGrammar() {

        return new Grammar(new HashMap<>(refinedGrammarRules));
    }

    /**
     * The outermost loop of the fixpoint iteration in the grammar refinement procedure.
     */
    private void refineGrammar() {

        do {
            hasAddedNewRules = false ;
            // this sets hasAddedNewRule to true whenever at least one rule has been added.
            findRefinedRules();
        } while(hasAddedNewRules);
    }

    /**
     * Attempts to find rules and suitable automaton states that can be refined to to new rules.
     */
    private void findRefinedRules() {

        for(Nonterminal lhs : grammar.getAllLeftHandSides()) {
            for(HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                findRefinementsOfRule(lhs, rhs);
            }
        }
    }

    private void findRefinementsOfRule(Nonterminal lhs, HeapConfiguration rhs) {

        if(rhs.countNonterminalEdges() > 0) {
            findRefinementsOfRuleWithNts(lhs, rhs);
        } else {
            findRefinementsOfRuleWithoutNts(lhs, rhs);
        }
    }

    private void findRefinementsOfRuleWithoutNts(Nonterminal lhs, HeapConfiguration rhs) {

        attemptAddRefinedRule(lhs, new ArrayList<>(), rhs);
    }

    /**
     * Attempts to add all possible refinements of the rule lhs -> rhs.
     * This means that all possible assignments of automaton states to nonterminals have to be considered.
     *
     * @param lhs The left-hand side of a grammar rule.
     * @param rhs The right-hand side of a grammar rule.
     */
    private void findRefinementsOfRuleWithNts(Nonterminal lhs, HeapConfiguration rhs) {

        AssignmentIterator<HeapAutomatonState> choices = new AssignmentIterator<>(getPossibleStateChoices(rhs));
        while(choices.hasNext()) {
            List<HeapAutomatonState> ntAssignment = choices.next();
            attemptAddRefinedRule(lhs, ntAssignment, rhs);
        }
    }

    /**
     * Attempts to add a refined rule based on the given original rule and assignment of states to nonterminal edges.
     * @param lhs The left-hand side of the original rule.
     * @param ntAssignment The assignment of automaton states to nonterminal edges of the rule's right-hand side.
     * @param rhs The right-hand side of the original rule.
     */
    private void attemptAddRefinedRule(Nonterminal lhs, List<HeapAutomatonState> ntAssignment, HeapConfiguration rhs) {
        HeapAutomatonState state = transitions.move(ntAssignment, rhs);
        if(!getFoundStates(lhs).contains(state)) {
            createRefinedRule(lhs, state, ntAssignment, rhs);
            hasAddedNewRules = true;
        }
    }

    private List<HeapAutomatonState> getFoundStates(Nonterminal nt) {

        if(!foundStates.containsKey(nt)) {
            foundStates.put(nt, new ArrayList<>());
        }
        return foundStates.get(nt);
    }

    /**
     * Computes for every nonterminal of the given heap configuration a list of all possible
     * heap automaton states that may be assigned to that nonterminal.
     *
     * @param rhs The heap configuration whose nonterminals shall be considered.
     * @return A list assigning a list of states to every nonterminal edge of rhs.
     */
    private List<List<HeapAutomatonState>> getPossibleStateChoices(HeapConfiguration rhs) {

        List<List<HeapAutomatonState>> result = new ArrayList<>();
        TIntIterator ntIterator = rhs.nonterminalEdges().iterator();
        while(ntIterator.hasNext()) {
            int edge = ntIterator.next();
            Nonterminal nt = rhs.labelOf(edge);
            result.add(getFoundStates(nt));
        }
        return result;
    }

    /**
     * Adds a new refined rule according to the considered state assignment.
     * @param lhs The left-hand side of the original rule that is refined.
     * @param state The state assigned to the left-hand side of the refined rule.
     * @param ntAssignment The states assigned to the nonterminals on the right-hand size of the refined rule.
     * @param rhs The right-hand side of the original rule that is refined.
     */
    private void createRefinedRule(Nonterminal lhs, HeapAutomatonState state,
                                   List<HeapAutomatonState> ntAssignment, HeapConfiguration rhs) {

        rhs = rhs.clone();
        HeapConfigurationBuilder builder = rhs.builder();
        TIntArrayList ntEdges = rhs.nonterminalEdges();
        for(int i=0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            HeapAutomatonState assignedState = ntAssignment.get(i);
            Nonterminal label = rhs.labelOf(edge);
            Nonterminal newLabel = new StateAnnotatedNonterminal(label, assignedState);
            builder.replaceNonterminal(edge, newLabel);
        }
        builder.build();
        StateAnnotatedNonterminal newLhs = new StateAnnotatedNonterminal(lhs, state);
        getFoundStates(lhs).add(state);
        getRefinedRules(newLhs).add(rhs);
    }

    private Set<HeapConfiguration> getRefinedRules(Nonterminal nt) {

        if(!refinedGrammarRules.containsKey(nt)) {
            refinedGrammarRules.put(nt, new HashSet<>());
        }
        return refinedGrammarRules.get(nt);
    }
}
