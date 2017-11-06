package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;

/**
 * The options passed to every Semantics object by a state space generator to configure the symbolic execution.
 * @author Christoph
 */
public class StateSpaceGeneratorSemanticsOptions implements SemanticsOptions {

    /**
     * The state space generator that calls Semantics objects during the symbolic execution.
     */
    private StateSpaceGenerator stateSpaceGenerator;

    public StateSpaceGeneratorSemanticsOptions(StateSpaceGenerator stateSpaceGenerator) {

        this.stateSpaceGenerator = stateSpaceGenerator;
    }

    @Override
    public StateSpace generateStateSpace(Program program, HeapConfiguration input, int scopeDepth)
            throws StateSpaceGenerationAbortedException {

        ProgramState initialState = Settings.getInstance().factory().createProgramState(input, scopeDepth);
        return StateSpaceGenerator.builder()
                .setAbortStrategy(stateSpaceGenerator.getAbortStrategy())
                .setCanonizationStrategy(stateSpaceGenerator.getCanonizationStrategy())
                .setMaterializationStrategy(stateSpaceGenerator.getMaterializationStrategy())
                .setStateLabelingStrategy(stateSpaceGenerator.getStateLabelingStrategy())
                .setStateRefinementStrategy(stateSpaceGenerator.getStateRefinementStrategy())
                .setDeadVariableElimination(stateSpaceGenerator.deadVariableEliminationEnabled)
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
