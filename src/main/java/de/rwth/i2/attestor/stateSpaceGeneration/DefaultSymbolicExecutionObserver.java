package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * The options passed to every SemanticsCommand object by a state space generator to configure the symbolic execution.
 *
 * @author Christoph
 */
public class DefaultSymbolicExecutionObserver implements SymbolicExecutionObserver {

    /**
     * The state space generator that calls SemanticsCommand objects during the symbolic execution.
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

        ProgramState initialState = stateSpaceGenerator.scene().createProgramState(heap);
        return StateSpaceGenerator.builder(stateSpaceGenerator)
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
