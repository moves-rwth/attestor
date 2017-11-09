package de.rwth.i2.attestor.stateSpaceGeneration;

/**
 * Functional interface to create {@link SemanticsObserver} objects during state space generation.
 * This is required, for example, if a new state space generation needs to be started due to newly
 * encountered procedure calls.
 *
 * @author Christoph
 */
@FunctionalInterface
public interface SemanticsObserverSupplier {

    /**
     * Provides a new SemanticsObserver configured for the given state space generator.
     * @param generator The state space generator that uses the semantics observer.
     * @return The configured SemanticsObserver object.
     */
    SemanticsObserver get(StateSpaceGenerator generator);
}
