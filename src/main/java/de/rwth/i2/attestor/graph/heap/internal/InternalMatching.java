package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.morphism.Morphism;

/**
 * Implementation of {@link Matching} for {@link InternalHeapConfiguration}.
 * Essentially this class wraps a {@link Morphism}.
 *
 * @author Christoph
 */
public class InternalMatching implements Matching {

    /**
     * The pattern HeapConfiguration whose underlying Graph is used by morphism.
     */
    private InternalHeapConfiguration pattern;

    /**
     * The target HeapConfiguration whose underlying Graph is used by morphism.
     */
    private InternalHeapConfiguration target;

    /**
     * The encapsulated Morphism that maps elements from pattern to target.
     */
    private Morphism morphism;

    /**
     * Initializes a new InternalMatching.
     *
     * @param pattern  The pattern HeapConfiguration whose underlying Graph is used by morphism.
     * @param morphism The target HeapConfiguration whose underlying Graph is used by morphism.
     * @param target   The encapsulated Morphism that maps elements from pattern to target.
     */
    public InternalMatching(HeapConfiguration pattern, Morphism morphism, HeapConfiguration target) {

        if (pattern == null || morphism == null) {
            throw new NullPointerException();
        }

        this.pattern = (InternalHeapConfiguration) pattern;
        this.morphism = morphism;
        this.target = (InternalHeapConfiguration) target;
    }

    @Override
    public HeapConfiguration pattern() {

        return pattern;
    }

    /**
     * Directly match elements using private IDs.
     *
     * @param element private ID of an
     * @return corresponding private ID
     */
    public int internalMatch(int element) {

        return morphism.match(element);
    }

    @Override
    public int match(int element) {

        int match = morphism.match(element);
        return target.getPublicId(match);
    }

}
