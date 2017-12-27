package obsolete;

import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;

/**
 * Functional interface to create {@link SymbolicExecutionObserver} objects during state space generation.
 * This is required, for example, if a new state space generation needs to be started due to newly
 * encountered procedure calls.
 *
 * @author Christoph
 */
@FunctionalInterface
public interface SemanticsObserverSupplier {

    /**
     * Provides a new SymbolicExecutionObserver configured for the given state space generator.
     *
     * @param generator The state space generator that uses the semantics observer.
     * @return The configured SymbolicExecutionObserver object.
     */
    SymbolicExecutionObserver get(StateSpaceGenerator generator);
}
