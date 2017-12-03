package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.Set;

public interface LabelledProgramState {

    /**
     * Checks whether this object satisfies an atomic proposition.
     *
     * @param ap, the proposition the state is checked for
     * @return true, if the proposition holds, i.e. is contained in the label of the state
     * false, otherwise
     */
    boolean satisfiesAP(String ap);

    /**
     * Adds an atomic proposition.
     *
     * @param ap The atomic proposition to add.
     */
    void addAP(String ap);

    /**
     * @return The set of all atomic propositions attached to this object.
     */
    Set<String> getAPs();
}
