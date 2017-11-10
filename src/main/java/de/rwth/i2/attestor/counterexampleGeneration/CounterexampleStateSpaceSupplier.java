package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Supplier class to initialize configured state space objects during counterexample generation.
 *
 * @author Christoph
 */
final class CounterexampleStateSpaceSupplier implements StateSpaceSupplier {

    private final Program program;
    private final CanonicalizationStrategy canonicalizationStrategy;

    private Set<ProgramState> finalStatesOfPreviousProcedure;
    private InvokeCleanup invokeCleanupOfPreviousProcedure;

    CounterexampleStateSpaceSupplier(Program program, CanonicalizationStrategy canonicalizationStrategy) {

        this.program = program;
        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    void setFinalStatesOfPreviousProcedure(Set<ProgramState> states) {

        this.finalStatesOfPreviousProcedure = states;
    }

    void setInvokeCleanupOfPreviousProcedure(InvokeCleanup invokeCleanup) {

        this.invokeCleanupOfPreviousProcedure = invokeCleanup;
    }

    @Override
    public StateSpace get() {

        assert finalStatesOfPreviousProcedure != null;

        Set<ProgramState> requiredFinalStates = new HashSet<>(finalStatesOfPreviousProcedure.size());
        for(ProgramState state : finalStatesOfPreviousProcedure) {
            requiredFinalStates.add(state.shallowCopyUpdatePC(-1));
        }

        CounterexampleStateSpace result = new CounterexampleStateSpace(
                program,
                canonicalizationStrategy,
                requiredFinalStates,
                invokeCleanupOfPreviousProcedure
        );

        finalStatesOfPreviousProcedure = null;
        invokeCleanupOfPreviousProcedure = null;

        return result;
    }
}
