package de.rwth.i2.attestor.automata.implementations.balancedness;

import de.rwth.i2.attestor.automata.AutomatonState;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A heap automaton determining whether a binary tree is balanced has two possible states:
 * 'balanced' and 'not balanced'
 * All other information are directly encoded in heap configurations using selector edge
 * annotations.
 *
 * @author Christoph
 */
class BalancedTreeAutomatonState implements AutomatonState {

    /**
     * Atomic proposition indicating a balanced tree.
     */
    private final static String AP_BALANCED = "balanced";

    /**
     * Atomic proposition indicating that a heap configuration is not
     * a balanced tree.
     */
    private final static String AP_NOT_BALANCED = "not balanced";

    /**
     * True if and only if this state corresponds to a balanced binary tree
     * and thus a final state.
     */
    private boolean isFinalState;

    /**
     * @param isFinalState True if and only if this state shall represent balanced binary trees.
     */
    BalancedTreeAutomatonState(boolean isFinalState) {

        this.isFinalState = isFinalState;
    }

    @Override
    public boolean isFinal() {

        return isFinalState;
    }

    @Override
    public Set<String> getAtomicPropositions() {

        Set<String> result = new HashSet<>(1);
        if(isFinalState) {
           result.add(AP_BALANCED);
        } else {
           result.add(AP_NOT_BALANCED);
        }
        return result;
    }

    @Override
    public Set<String> getAllAtomicPropositions() {

        Set<String> result = new HashSet<>(2);
        result.add(AP_BALANCED);
        result.add(AP_NOT_BALANCED);
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof BalancedTreeAutomatonState
                && ((BalancedTreeAutomatonState) obj).isFinalState == isFinalState;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isFinalState);
    }
}
