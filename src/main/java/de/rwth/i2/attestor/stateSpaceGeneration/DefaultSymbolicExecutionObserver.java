package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;

/**
 * The options passed to every Semantics object by a state space generator to configure the symbolic execution.
 * @author Christoph
 */
public class DefaultSymbolicExecutionObserver implements SymbolicExecutionObserver {

    /**
     * The state space generator that calls Semantics objects during the symbolic execution.
     */
    private final StateSpaceGenerator stateSpaceGenerator;

    public DefaultSymbolicExecutionObserver(StateSpaceGenerator stateSpaceGenerator) {

        this.stateSpaceGenerator = stateSpaceGenerator;
    }

    @Override
    public void update(Object handler, ProgramState input) {

    }

    @Override
    public StateSpace generateStateSpace(Program program, ProgramState input)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration heap = input.getHeap();
        int scopeDepth = input.getScopeDepth();

        ProgramState initialState = Settings.getInstance().factory().createProgramState(heap, scopeDepth);
        return StateSpaceGenerator.builder()
                .setAbortStrategy(stateSpaceGenerator.getAbortStrategy())
                .setCanonizationStrategy(stateSpaceGenerator.getCanonizationStrategy())
                .setMaterializationStrategy(stateSpaceGenerator.getMaterializationStrategy())
                .setStateLabelingStrategy(stateSpaceGenerator.getStateLabelingStrategy())
                .setStateRefinementStrategy(stateSpaceGenerator.getStateRefinementStrategy())
                .setDeadVariableElimination(stateSpaceGenerator.isDeadVariableEliminationEnabled())
                .setBreadthFirstSearchEnabled(stateSpaceGenerator.isBreadthFirstSearchEnabled())
                .setExplorationStrategy(stateSpaceGenerator.getExplorationStrategy())
                .setStateSpaceSupplier(stateSpaceGenerator.getStateSpaceSupplier())
                .setSemanticsOptionsSupplier(stateSpaceGenerator.getSemanticsObserverSupplier())
                .setStateCounter(stateSpaceGenerator.getTotalStatesCounter())
                .setProgram(program)
                .addInitialState(initialState)
                .build()
                .generate();
    }

    @Override
    public boolean isDeadVariableEliminationEnabled() {

        return stateSpaceGenerator.isDeadVariableEliminationEnabled();
    }
}
