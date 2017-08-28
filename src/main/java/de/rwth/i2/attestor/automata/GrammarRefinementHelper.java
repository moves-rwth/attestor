package de.rwth.i2.attestor.automata;

import java.util.*;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.RefinedDefaultNonterminal;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

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

    private HeapAutomaton automaton;

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
    private Map<Nonterminal, List<AutomatonState>> foundStates;

    /**
     * The rules of the refined grammar constructed so far.
     */
    private Map<Nonterminal, Set<HeapConfiguration>> refinedGrammarRules;

    /**
     * Refines a given grammar according to the transition relation of a heap automaton.
     * @param grammar The grammar that should be refined.
     * @param automaton The heap automaton guiding the refinement.
     */
    GrammarRefinementHelper(Grammar grammar, HeapAutomaton automaton) {

        this.grammar = grammar;
        this.automaton = automaton;
        foundStates = new HashMap<>();
        refinedGrammarRules = new HashMap<>();
        refineGrammar();
    }

    /**
     * @return The refined grammar.
     */
    Grammar getRefinedGrammar() {

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
     * Attempts to find refined rules for every previously existing rule.
     */
    private void findRefinedRules() {

        for(Nonterminal lhs : grammar.getAllLeftHandSides()) {
            for(HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                findRefinementsOfRule(lhs, rhs);
            }
        }
    }

    /**
     * Attempt to find refined rules of the rule lhs -> rhs
     * @param lhs The left-hand side of the rule
     * @param rhs The right-hand side of the rule
     */
    private void findRefinementsOfRule(Nonterminal lhs, HeapConfiguration rhs) {

        if(rhs.countNonterminalEdges() > 0) {
            findRefinementsOfRuleWithNts(lhs, rhs);
        } else {
            findRefinementsOfRuleWithoutNts(lhs, rhs);
        }
    }

    /**
     * Attempt to find refined rules of the rule lhs -> rhs, where rhs contains no nonterminals.
     * @param lhs The left-hand side of the rule
     * @param rhs The right-hand side of the rule
     */
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

        List<List<AutomatonState>> possibleAutomatonStates = getPossibleStateChoices(rhs);
        AssignmentIterator<AutomatonState> choices = new AssignmentIterator<>(possibleAutomatonStates);
        while(choices.hasNext()) {
            List<AutomatonState> ntAssignment = choices.next();
            attemptAddRefinedRule(lhs, ntAssignment, rhs);
        }
    }

    /**
     * Attempts to add a refined rule based on the given original rule and assignment of states to nonterminal edges.
     * @param lhs The left-hand side of the original rule.
     * @param ntAssignment The assignment of automaton states to nonterminal edges of the rule's right-hand side.
     * @param rhs The right-hand side of the original rule.
     */
    private void attemptAddRefinedRule(Nonterminal lhs, List<AutomatonState> ntAssignment, HeapConfiguration rhs) {
        AutomatonState state = automaton.move(ntAssignment, rhs);

        Nonterminal refinedLhs = createRefinedLhs(lhs, state);
        HeapConfiguration refinedRhs = createRefinedRhs(ntAssignment, rhs);

        Set<HeapConfiguration> refinedLhsRules = getRefinedRules(refinedLhs);
        if(!refinedLhsRules.contains(refinedRhs)) {
            refinedLhsRules.add(refinedRhs);
            hasAddedNewRules = true;
        }
    }

    private List<AutomatonState> getFoundStates(Nonterminal nt) {

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
    private List<List<AutomatonState>> getPossibleStateChoices(HeapConfiguration rhs) {

        List<List<AutomatonState>> result = new ArrayList<>();
        TIntIterator ntIterator = rhs.nonterminalEdges().iterator();
        while(ntIterator.hasNext()) {
            int edge = ntIterator.next();
            Nonterminal nt = rhs.labelOf(edge);
            result.add(getFoundStates(nt));
        }
        return result;
    }

    /**
     * Determines the refined left-hand side obtained from a nonterminal and a state of a heap automaton.
     * @param lhs A nonterminal that is the original left-hand side of a rule.
     * @param state The state of a heap automaton used for refinement.
     * @return The refined nonterminal.
     */
    private Nonterminal createRefinedLhs(Nonterminal lhs, AutomatonState state) {

        RefinedNonterminal newLhs = new RefinedDefaultNonterminal(lhs, state);
        List<AutomatonState> lhsFoundStates = getFoundStates(lhs);
        if(!lhsFoundStates.contains(state)) {
            lhsFoundStates.add(state);
        }
        return newLhs;
    }

    /**
     * Determines the refined right-hand side obtained from a heap configuration and an assignment of
     * heap automaton states to each of its nonterminal hyperedges.
     * @param ntAssignment A list assigning a state to every hyperedge of rhs.
     * @param rhs A heap configuration corresponding to the original right-hand side of rule.
     * @return The refined right-hand side of a rule.
     */
    private HeapConfiguration createRefinedRhs(List<AutomatonState> ntAssignment, HeapConfiguration rhs) {

        rhs = rhs.clone();
        HeapConfigurationBuilder builder = rhs.builder();
        TIntArrayList ntEdges = rhs.nonterminalEdges();
        for(int i=0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            AutomatonState assignedState = ntAssignment.get(i);
            Nonterminal label = rhs.labelOf(edge);
            Nonterminal newLabel = new RefinedDefaultNonterminal(label, assignedState);
            builder.replaceNonterminal(edge, newLabel);
        }
        return builder.build();
    }

    private Set<HeapConfiguration> getRefinedRules(Nonterminal nt) {

        if(!refinedGrammarRules.containsKey(nt)) {
            refinedGrammarRules.put(nt, new HashSet<>());
        }
        return refinedGrammarRules.get(nt);
    }
}
