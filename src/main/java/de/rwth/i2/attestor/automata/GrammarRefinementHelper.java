package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.tasks.StateAnnotatedNonterminal;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

/**
 * Created by cmath on 7/19/17.
 */
class GrammarRefinementHelper {

    private Grammar grammar;
    private HeapAutomatonTransition transitions;

    private List<StateAnnotatedNonterminal> reachableNonterminals;
    private Map<Nonterminal, List<HeapAutomatonState>> foundStates;
    private Map<Nonterminal, Set<HeapConfiguration>> grammarRules;

    List<List<HeapAutomatonState>> stateChoices = getPossibleStateChoices();

    public GrammarRefinementHelper(Grammar grammar, HeapAutomatonTransition transitions) {

        this.grammar = grammar;
        this.transitions = transitions;
        nonterminals = new ArrayList<>();
        grammarRules = new HashMap<>();

        refineGrammar();
    }

    private void refineGrammar() {

        int previousSizeReachableNonterminals;

        do {
            previousSizeReachableNonterminals = reachableNonterminals.size();
            updateReachableNonterminals();
        } while( previousSizeReachableNonterminals != reachableNonterminals.size() );
    }

    private void updateReachableNonterminals() {

        for(Nonterminal lhs : grammar.getAllLeftHandSides()) {
            for(HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                refineRule(lhs, rhs);
            }
        }
    }

    private void refineRule(Nonterminal lhs, HeapConfiguration rhs) {

        List<List<HeapAutomatonState>> stateChoices = getPossibleStateChoices();
        TIntArrayList possibleChoices = new TIntArrayList(stateChoices.size());
        ChoicesIterator choices = new ChoicesIterator(possibleChoices);
        while(choices.hasNext()) {
            TIntArrayList currentChoice = choices.next();
        }


        while(pos < stateChoices.size()) {

            int next = currentChoice.get(pos) + 1;
            if(next < stateChoices.get(pos).size()) {
                currentChoice.set(pos, next);
                for(int i=0; i < pos; i++) {
                    currentChoice.set(i, 0);
                }
                pos = 0;

                List<HeapAutomatonState> ntAssignment = new ArrayList<>(currentChoice.size());
                for(int i=0; i < currentChoice.size(); i++) {
                    ntAssignment.add(stateChoices.get(i).get(currentChoice.get(i)));
                }
                HeapAutomatonState state = transitions.move(ntAssignment, rhs);
                foundStates.get(lhs).add(state);
                // update rules
                HeapConfiguration newRhs = rhs.clone();
                updateRule();

            } else {
                pos++;
                continue;
            }


        }


    }

    private List<List<HeapAutomatonState>> getPossibleStateChoices() {
        List<List<HeapAutomatonState>> result = new ArrayList<>();
        TIntIterator ntIterator = rhs.nonterminalEdges().iterator();
        while(ntIterator.hasNext()) {
            int edge = ntIterator.next();
            Nonterminal nt = rhs.labelOf(edge);
            result.add(foundStates.get(nt));
        }
        return result;
    }

}
