package de.rwth.i2.attestor.graph.morphism;

import java.util.Arrays;

/**
 * Implementation of a morphism between two {@link Graph}s.
 * In this setting, a morphism corresponds to a mapping between nodes of a pattern graph
 * to nodes of a target graph.
 *
 * @author Christoph
 */
public class Morphism {

    /**
     * A mapping between nodes of a pattern graph (indices of the array)
     * to nodes in a target graph (values of the array).
     */
    private final int[] mapping;

    /**
     * Initializes the morphism
     *
     * @param mapping The mapping between node identifiers of a pattern graph and a target graph.
     */
    public Morphism(int[] mapping) {

        this.mapping = Arrays.copyOf(mapping, mapping.length);
    }

    /**
     * Provides the target node corresponding to a given pattern node.
     *
     * @param patternNode A node in the pattern graph.
     * @return The corresponding node in the target graph.
     */
    public int match(int patternNode) {

        return mapping[patternNode];
    }
}
