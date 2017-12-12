package de.rwth.i2.attestor.graph.morphism;

/**
 * A MorphismChecker takes two graphs, called pattern and target, and computes
 * all graph morphisms from the pattern graph into the target graph.
 *
 * @author Christoph
 */
public interface MorphismChecker {


    /**
     * Sets the pattern and target graph and searches for the first graph morphism.
     * This method is not reentrant.
     *
     * @param pattern The pattern graph.
     * @param target  The target graph.
     */
    void run(Graph pattern, Graph target);

    /**
     * @return True if and only if at least one Morphism has been found after calling run().
     */
    boolean hasMorphism();

    /**
     * @return The found Morphism or null if no Morphism has been found (yet).
     */
    Morphism getMorphism();
}
