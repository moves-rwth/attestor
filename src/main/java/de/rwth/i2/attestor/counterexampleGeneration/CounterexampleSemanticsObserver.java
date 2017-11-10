package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.Collections;
import java.util.Set;

/**
 * A tailored observer that determines the required successor states of
 * procedure calls to guide the counterexample generation.
 *
 * @author Christoph
 */
final class CounterexampleSemanticsObserver implements SemanticsObserver {

    private final StateSpaceGenerator stateSpaceGenerator;
    private final CounterexampleStateSpaceSupplier stateSpaceSupplier;
    private final Trace trace;

    private ProgramState requiredFinalState = null;
    private int requiredNoOfFinalStates = 1;

    CounterexampleSemanticsObserver(StateSpaceGenerator stateSpaceGenerator,
                                    Trace trace) {

        this.stateSpaceGenerator = stateSpaceGenerator;
        this.stateSpaceSupplier = (CounterexampleStateSpaceSupplier) stateSpaceGenerator.getStateSpaceSupplier();
        this.trace = trace;

    }

    @Override
    public void update(Object handler, ProgramState input) {

        if(input.getScopeDepth() == 0 && handler instanceof InvokeCleanup) {
            updateInvoke((InvokeCleanup) handler, input);
        } else if(handler instanceof AbstractMethod) {
            updateMethod( (AbstractMethod) handler, input);
        }
    }

    private void updateInvoke(InvokeCleanup invokeCleanup, ProgramState input) {
        requiredFinalState = trace.getSuccessor(input);
        stateSpaceSupplier.setInvokeCleanupOfPreviousProcedure(invokeCleanup);
    }

    private void updateMethod(AbstractMethod method, ProgramState input) {

        method.setReuseResults(false);
        if(requiredFinalState != null) {
            requiredNoOfFinalStates = 1;
            stateSpaceSupplier.setFinalStatesOfPreviousProcedure(
                    Collections.singleton(requiredFinalState)
            );
        } else {
            Set<ProgramState> finalStates = method.getFinalStates(input.getHeap());
            stateSpaceSupplier.setFinalStatesOfPreviousProcedure(finalStates);
            requiredNoOfFinalStates = finalStates.size();
        }
    }

    @Override
    public StateSpace generateStateSpace(Program program, ProgramState input)
            throws StateSpaceGenerationAbortedException {

        return StateSpaceGenerator
                .builder()
                .setAbortStrategy(stateSpaceGenerator.getAbortStrategy())
                .setCanonizationStrategy(stateSpaceGenerator.getCanonizationStrategy())
                .setMaterializationStrategy(stateSpaceGenerator.getMaterializationStrategy())
                .setStateLabelingStrategy(stateSpaceGenerator.getStateLabelingStrategy())
                .setStateRefinementStrategy(stateSpaceGenerator.getStateRefinementStrategy())
                .setDeadVariableElimination(stateSpaceGenerator.isDeadVariableEliminationEnabled())
                .setBreadthFirstSearchEnabled(stateSpaceGenerator.isBreadthFirstSearchEnabled())
                .setExplorationStrategy(new CounterexampleExplorationStrategy())
                .setStateSpaceSupplier(stateSpaceGenerator.getStateSpaceSupplier())
                .setSemanticsOptionsSupplier(stateSpaceGenerator.getSemanticsObserverSupplier())
                .setStateCounter(stateSpaceGenerator.getTotalStatesCounter())
                .setProgram(program)
                .addInitialState(input)
                .build()
                .generate();
    }

    private final class CounterexampleExplorationStrategy implements ExplorationStrategy {
        @Override
        public boolean check(ProgramState state, StateSpace stateSpace) {
            return stateSpace.getFinalStates().size() < requiredNoOfFinalStates;
        }
    }

    @Override
    public boolean isDeadVariableEliminationEnabled() {

        return stateSpaceGenerator.isDeadVariableEliminationEnabled();
    }
}
