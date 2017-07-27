package de.rwth.i2.attestor.automata.implementations.balancedness;

import de.rwth.i2.attestor.automata.AutomatonState;

import java.util.HashSet;
import java.util.Set;

public class BalancedTreeAutomatonState implements AutomatonState {

    private final static String AP_BALANCED = "balanced";
    private final static String AP_NOT_BALANCED = "not balanced";

    private boolean isFinalState;

    public BalancedTreeAutomatonState(boolean isFinalState) {

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

        if(obj instanceof BalancedTreeAutomatonState) {
            return ((BalancedTreeAutomatonState) obj).isFinalState == isFinalState;
        }
        return false;
    }
}
