package de.rwth.i2.attestor.graph.morphism.checkers;

import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.Morphism;
import de.rwth.i2.attestor.graph.morphism.MorphismChecker;
import de.rwth.i2.attestor.graph.morphism.VF2Algorithm;

/**
 * An abstract class providing a default implementation of {@link MorphismChecker}
 * to find graph morphisms mapping a pattern graph into a target graph.
 * <p>
 * Subclasses of AbstractVF2MorphismChecker usually determine the actual algorithm
 * that is applied to find morphisms.
 * <p>
 * The class supports to check whether at least one morphism exists and to iteratively
 * get all existing morphisms.
 *
 * @author Christoph
 */
public abstract class AbstractVF2MorphismChecker implements MorphismChecker {

    /**
     * The algorithm that is used to determine graph morphisms.
     */
    private final VF2Algorithm matchingAlgorithm;
    /**
     * Stores whether at least one morphism could be found.
     */
    private boolean hasMorphism;
    /**
     * The morphism that could be found. Null otherwise.
     */
    private Morphism foundMorphism;

    /**
     * Initializes this checker.
     *
     * @param matchingAlgorithm The algorithm to determine graph morphisms.
     */
    AbstractVF2MorphismChecker(VF2Algorithm matchingAlgorithm) {

        this.matchingAlgorithm = matchingAlgorithm;
    }

    /**
     * Starts searching for graph morphisms of the pattern graph into the target graph.
     *
     * @param pattern The pattern graph.
     * @param target  The target graph.
     */
    public void run(Graph pattern, Graph target) {

        hasMorphism = matchingAlgorithm.match(pattern, target);
        foundMorphism = matchingAlgorithm.getMorphism();
    }

    @Override
    public boolean hasMorphism() {

        return hasMorphism;
    }

    @Override
    public Morphism getMorphism() {

        return foundMorphism;
    }
}
