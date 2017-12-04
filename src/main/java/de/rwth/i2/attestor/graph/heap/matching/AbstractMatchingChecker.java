package de.rwth.i2.attestor.graph.heap.matching;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.InternalMatching;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.MorphismChecker;

/**
 * A general abstract wrapper to compute a {@link Matching} between two HeapConfigurations.
 * The exact matching to be computed has to be determined by subclasses.
 *
 * @author Christoph
 * @see MorphismChecker
 */
public abstract class AbstractMatchingChecker {

    /**
     * The pattern HeapConfiguration that should be embedded in a target HeapConfiguration
     */
    private HeapConfiguration pattern;

    /**
     * The target HeapConfiguration in which the pattern is embedded
     */
    private HeapConfiguration target;

    /**
     * The wrapped {@link MorphismChecker} that computes Matchings between the graphs underlying
     * pattern and target.
     */
    private MorphismChecker checker;


    /**
     * Initializes an AbstractMatchingChecker.
     *
     * @param pattern The pattern HeapConfiguration.
     * @param target  The target HeapConfiguration.
     * @param checker The underlying {@link MorphismChecker} that determines the kind of matching to compute.
     */
    AbstractMatchingChecker(HeapConfiguration pattern, HeapConfiguration target, MorphismChecker checker) {


        if (!(pattern instanceof Graph)) {
            throw new IllegalArgumentException("Provided pattern is not a Graph.");
        }

        if (!(target instanceof Graph)) {
            throw new IllegalArgumentException("Provided target is not a Graph.");
        }

        if (checker == null) {
            throw new NullPointerException();
        }

        this.pattern = pattern;
        this.target = target;
        this.checker = checker;

        Graph p = (Graph) pattern;
        Graph t = (Graph) target;

        this.checker.run(p, t);
    }

    /**
     * @return The pattern HeapConfiguration.
     */
    public HeapConfiguration getPattern() {

        return pattern;
    }

    /**
     * @return The target HeapConfiguration.
     */
    public HeapConfiguration getTarget() {

        return target;
    }

    /**
     * @return True if and only if at least one matching has been found in total.
     */
    public boolean hasMatching() {

        return checker.hasMorphism();
    }

    /**
     * @return The matching that has been found or null if no matching has been found
     */
    public Matching getMatching() {

        return new InternalMatching(pattern, checker.getMorphism(), target);
    }
}
