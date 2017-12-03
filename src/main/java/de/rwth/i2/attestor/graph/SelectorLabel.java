package de.rwth.i2.attestor.graph;


/**
 * General Interface for labels of selector edges.
 *
 * @author Christoph
 */
public interface SelectorLabel extends Comparable<SelectorLabel> {

    /**
     * @param label The label that should be checked.
     * @return True if and only if the given SelectorLabel has the provided label.
     */
    boolean hasLabel(String label);

    /**
     * @return The label as a String corresponding to this SelectorLabel.
     */
    String getLabel();
}
