package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.ipa.FragmentedHeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Supplier class to initialize configured state space objects during counterexample generation.
 *
 * @author Christoph
 */
final class CounterexampleStateSpaceSupplier implements StateSpaceSupplier {

    private final StateCanonicalizationStrategy canonicalizationStrategy;

    private Set<ProgramState> finalStatesOfPreviousProcedure;
    private InvokeCleanup invokeCleanupOfPreviousProcedure;

    private SymbolicExecutionObserver invokeObserverOfPreviousProcedure = null;
    private FragmentedHeapConfiguration fragmentedHcOfPreviousProcedure = null;

    CounterexampleStateSpaceSupplier(StateCanonicalizationStrategy canonicalizationStrategy) {

        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    void setFinalStatesOfPreviousProcedure(Set<ProgramState> states) {

        this.finalStatesOfPreviousProcedure = states;
    }

    void setInvokeCleanupOfPreviousProcedure(InvokeCleanup invokeCleanup, SymbolicExecutionObserver observer) {

        this.invokeCleanupOfPreviousProcedure = invokeCleanup;
        this.invokeObserverOfPreviousProcedure = observer;
    }

    void setFragmentedHcOfPreviousProcedure(FragmentedHeapConfiguration fragmentedHc) {

        this.fragmentedHcOfPreviousProcedure = fragmentedHc;
    }

    @Override
    public StateSpace get() {

        assert finalStatesOfPreviousProcedure != null;

        Set<ProgramState> requiredFinalStates = new LinkedHashSet<>(finalStatesOfPreviousProcedure.size());
        for (ProgramState state : finalStatesOfPreviousProcedure) {
            requiredFinalStates.add(state.shallowCopyUpdatePC(-1));
        }

        CounterexampleStateSpace result = new CounterexampleStateSpace(
                canonicalizationStrategy,
                requiredFinalStates,
                invokeCleanupOfPreviousProcedure,
                invokeObserverOfPreviousProcedure,
                fragmentedHcOfPreviousProcedure
        );

        finalStatesOfPreviousProcedure = null;
        invokeCleanupOfPreviousProcedure = null;
        invokeObserverOfPreviousProcedure = null;

        return result;
    }
}
