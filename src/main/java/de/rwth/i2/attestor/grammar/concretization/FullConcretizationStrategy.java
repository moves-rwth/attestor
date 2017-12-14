package de.rwth.i2.attestor.grammar.concretization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.List;

public interface FullConcretizationStrategy {

    /**
     * Determines a list of up to 'count' concrete heap configurations derived from the given one
     *
     * @param abstractHc The heap configuration from which concretizations are derived
     * @param count      The number of requested concrete heap configurations
     * @return A list of concrete heap configurations
     */
    List<HeapConfiguration> concretize(HeapConfiguration abstractHc, int count);
}
